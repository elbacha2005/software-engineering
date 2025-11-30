package CodeQuest.Main;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

// Manages all game sounds - background music and sound effects
public class SoundManager {
    private Clip musicClip; // Currently playing background music
    private Clip sfxClip; // Sound effects clip

    private URL menuMusic; // Menu background music
    private URL townMusic; // Game background music
    private URL navigationSfx; // Navigation sound effect

    private String currentMusic = ""; // Track currently playing music
    private boolean isLoadingMusic = false; // Prevent multiple loading threads
    private long lastNavigationSoundTime = 0; // Throttle navigation sounds
    private static final long NAVIGATION_SOUND_COOLDOWN = 100; // Minimum 100ms between sounds

    // Constructor: loads all sound files
    public SoundManager() {
        loadSounds();
    }

    // Load all sound files from resources
    private void loadSounds() {
        try {
            // Prefer .ogg files (smaller size, better quality/size ratio)
            menuMusic = getClass().getResource("/CodeQuest/res/audio/menu.wav");
            if (menuMusic == null) {
                menuMusic = getClass().getResource("/CodeQuest/res/audio/menu.wav");
                System.out.println("Using menu.wav (fallback)");
            } else {
                System.out.println("Loaded menu.ogg");
            }

            townMusic = getClass().getResource("/CodeQuest/res/audio/town.wav");
            if (townMusic == null) {
                townMusic = getClass().getResource("/CodeQuest/res/audio/town.wav");
                System.out.println("Using town.wav (fallback)");
            } else {
                System.out.println("Loaded town.ogg");
            }

            navigationSfx = getClass().getResource("/CodeQuest/res/audio/life_reg.wav");

            if (menuMusic == null) System.out.println("Warning: No menu music found");
            if (townMusic == null) System.out.println("Warning: No town music found");
            if (navigationSfx == null) System.out.println("Warning: life_reg.wav not found");
        } catch (Exception e) {
            System.out.println("Error loading sounds: " + e.getMessage());
        }
    }

    // Play background music (loops continuously)
    public void playMusic(String musicName) {
        // Don't reload if already playing this music
        if (currentMusic.equals(musicName) && musicClip != null && musicClip.isRunning()) {
            return;
        }

        // Don't start loading if already loading
        if (isLoadingMusic) {
            return;
        }

        // Don't reload if this music is queued to play
        if (currentMusic.equals(musicName)) {
            return;
        }

        isLoadingMusic = true;
        currentMusic = musicName;

        URL soundURL = null;
        switch (musicName) {
            case "menu":
                soundURL = menuMusic;
                break;
            case "town":
                soundURL = townMusic;
                break;
        }

        if (soundURL == null) {
            System.out.println("Sound not found: " + musicName);
            isLoadingMusic = false;
            return;
        }

        // Stop current music before loading new one
        stopMusic();

        // Load music asynchronously to avoid lag
        final URL finalSoundURL = soundURL;
        Thread musicThread = new Thread(() -> {
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(finalSoundURL);

                // Get audio format for potential conversion
                AudioFormat baseFormat = audioStream.getFormat();

                // Convert to PCM format if needed (reduces CPU usage)
                AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
                );

                AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, audioStream);

                musicClip = AudioSystem.getClip();
                musicClip.open(decodedStream);

                // Set volume based on music type
                if (musicName.equals("menu")) {
                    setMusicVolume(0.4f); // Lower volume for menu music
                } else {
                    setMusicVolume(0.7f); // Normal volume for game music
                }

                musicClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop forever
                musicClip.start();
            } catch (UnsupportedAudioFileException e) {
                System.out.println("Music file format not supported (likely .ogg). Continuing without background music.");
                System.out.println("For better performance, convert audio files:");
                System.out.println("  ffmpeg -i " + musicName + ".ogg -ar 22050 -ac 2 -b:a 128k " + musicName + ".wav");
            } catch (IOException | LineUnavailableException e) {
                System.out.println("Error loading music: " + e.getMessage());
            } finally {
                isLoadingMusic = false;
            }
        });
        musicThread.setDaemon(true); // Make thread daemon so it doesn't prevent JVM shutdown
        musicThread.start();
    }

    // Stop background music
    public void stopMusic() {
        if (musicClip != null) {
            if (musicClip.isRunning()) {
                musicClip.stop();
            }
            if (musicClip.isOpen()) {
                musicClip.close();
            }
            musicClip = null;
        }
    }

    // Play sound effect (navigation sound)
    public void playNavigationSound() {
        if (navigationSfx == null) return;

        // Throttle navigation sounds to prevent overlap
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastNavigationSoundTime < NAVIGATION_SOUND_COOLDOWN) {
            return; // Too soon, skip this sound
        }
        lastNavigationSoundTime = currentTime;

        // Play sound in separate thread to avoid blocking
        new Thread(() -> {
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(navigationSfx);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);

                // Auto-close clip when finished
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });

                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.out.println("Error playing navigation sound: " + e.getMessage());
            }
        }).start();
    }

    // Set music volume (0.0 to 1.0)
    public void setMusicVolume(float volume) {
        if (musicClip != null && musicClip.isOpen()) {
            try {
                FloatControl gainControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            } catch (Exception e) {
                System.out.println("Error setting volume: " + e.getMessage());
            }
        }
    }
}
