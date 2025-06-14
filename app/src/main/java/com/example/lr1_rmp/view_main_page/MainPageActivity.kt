package com.example.lr1_rmp.view_main_page

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.documentfile.provider.DocumentFile
import androidx.drawerlayout.widget.DrawerLayout
import com.example.lr1_rmp.R
import com.example.lr1_rmp.view_registration_and_authorization.MainActivity

class MainPageActivity : AppCompatActivity() {
    // Основные переменные
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var runnable: Runnable
    private lateinit var handler: Handler
    private var isPlaying = false
    private var currentSongUri: Uri? = null
    private var currentSongPosition = 0
    private var songsList: List<DocumentFile> = emptyList()

    companion object {
        private const val REQUEST_CODE_PICK_FOLDER = 1001
        private const val UPDATE_INTERVAL = 1000L // 1 секунда для обновления SeekBar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        // Настройка отступов под системные элементы
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Инициализация медиаплеера
        mediaPlayer = MediaPlayer()
        handler = Handler(Looper.getMainLooper())

        // Загрузка последней выбранной папки
        loadSavedFolder()

        // Настройка элементов интерфейса
        setupDrawer()
        setupWelcomeMessage()
        initControls()
    }

    private fun loadSavedFolder() {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
            .getString("selected_folder", null)
            ?.let { Uri.parse(it) }
            ?.let { findSong(it) }
    }

    private fun setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout)
        findViewById<ImageButton>(R.id.btn_open_menu).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setupWelcomeMessage() {
        val session = getSharedPreferences("session", Context.MODE_PRIVATE)
        findViewById<TextView>(R.id.helloText).text =
            "Добро пожаловать, ${session.getString("current_login_user", null)}"

        findViewById<Button>(R.id.exit_button).setOnClickListener {
            session.edit().putBoolean("session", false).apply()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun initControls() {
        // Инициализация элементов управления
        val playPauseBtn: ImageButton = findViewById(R.id.playPauseBtn)
        val seekBar: SeekBar = findViewById(R.id.seekBar)

        playPauseBtn.setOnClickListener {
            if (isPlaying) pauseMusic() else playMusic()
            playPauseBtn.setImageResource(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )
        }

        findViewById<ImageButton>(R.id.prevBtn).setOnClickListener { playPreviousSong() }
        findViewById<ImageButton>(R.id.nextBtn).setOnClickListener { playNextSong() }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer.seekTo(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Логика обновления SeekBar
        runnable = Runnable {
            seekBar.progress = mediaPlayer.currentPosition
            findViewById<TextView>(R.id.currentTime).text = formatTime(mediaPlayer.currentPosition)
            handler.postDelayed(runnable, UPDATE_INTERVAL)
        }
    }

    fun findSong(folderUri: Uri) {
        songsList = DocumentFile.fromTreeUri(this, folderUri)
            ?.listFiles()
            ?.filter { it.isFile && (it.name?.endsWith(".mp3") == true || it.name?.endsWith(".wav") == true) }
            ?: emptyList()

        findViewById<ListView>(R.id.ListSound).apply {
            adapter = ArrayAdapter(
                this@MainPageActivity,
                android.R.layout.simple_list_item_1,
                songsList.map { it.name?.removeSuffix(".mp3")?.removeSuffix(".wav") ?: "Unknown" }
            )
            setOnItemClickListener { _, _, position, _ ->
                playSelectedSong(position)
            }
        }
    }

    private fun playSelectedSong(position: Int) {
        currentSongPosition = position
        currentSongUri = songsList[position].uri

        // Обновление интерфейса
        findViewById<TextView>(R.id.songTitle).text =
            songsList[position].name?.removeSuffix(".mp3")?.removeSuffix(".wav") ?: "Unknown"
        findViewById<View>(R.id.controlsLayout).visibility = View.VISIBLE

        playSong(songsList[position].uri)
    }

    private fun playSong(uri: Uri) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(this, uri)
            mediaPlayer.prepare()
            mediaPlayer.start()
            isPlaying = true

            // Настройка SeekBar
            with(findViewById<SeekBar>(R.id.seekBar)) {
                max = mediaPlayer.duration
                progress = 0
            }
            findViewById<TextView>(R.id.totalTime).text = formatTime(mediaPlayer.duration)
            findViewById<ImageButton>(R.id.playPauseBtn).setImageResource(R.drawable.ic_pause)

            handler.postDelayed(runnable, UPDATE_INTERVAL)

            mediaPlayer.setOnCompletionListener { playNextSong() }
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка воспроизведения", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun playMusic() {
        if (!mediaPlayer.isPlaying && currentSongUri != null) {
            mediaPlayer.start()
            isPlaying = true
            handler.postDelayed(runnable, UPDATE_INTERVAL)
        }
    }

    private fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
            handler.removeCallbacks(runnable)
        }
    }

    private fun playNextSong() {
        if (songsList.isNotEmpty()) {
            currentSongPosition = (currentSongPosition + 1) % songsList.size
            playSelectedSong(currentSongPosition)
        }
    }

    private fun playPreviousSong() {
        if (songsList.isNotEmpty()) {
            currentSongPosition = (currentSongPosition - 1 + songsList.size) % songsList.size
            playSelectedSong(currentSongPosition)
        }
    }

    private fun formatTime(millis: Int): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    // Обработка выбора папки
    fun openFolderPicker(activity: Activity) {
        Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            activity.startActivityForResult(this, REQUEST_CODE_PICK_FOLDER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_FOLDER && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                saveSelectedFolder(uri)
                findSong(uri)
            }
        }
    }

    private fun saveSelectedFolder(uri: Uri) {
        getSharedPreferences("app_prefs", MODE_PRIVATE).edit()
            .putString("selected_folder", uri.toString())
            .apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(runnable)
    }
}