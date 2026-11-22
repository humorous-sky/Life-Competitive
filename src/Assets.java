

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;
import java.util.HashSet;

import java.util.Map;
import java.util.HashMap;

public class Assets {
	
	public static int loaded = 0;
	
	private static Queue<Clip> soundList= new LinkedList<Clip>();
	private static Queue<Integer> volumeList= new LinkedList<Integer>();

	public static Map<String, BufferedImage> images = new HashMap<String, BufferedImage>();
	public static Map<String, Clip> sounds = new HashMap<String, Clip>();
	public static Map<String, Font> fonts = new HashMap<String, Font>();


	public Assets() {
		
	}
	private static void newImage(String name) {
		System.out.println("Loading image: " + name);
		try {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			URL url = Life.class.getResource("assets/" + name);
			BufferedImage img = ImageIO.read(url);
			images.put(name, img);
			System.out.println("Image loaded: " + name);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to load image: " + name);
		}
	}
	private static void newSound(String name) {
		System.out.println("Loading sound: " + name);
		try {
			URL url = Life.class.getResource("sounds/" + name);
			AudioInputStream in = AudioSystem.getAudioInputStream(url);
			Clip clip = AudioSystem.getClip(); 
			clip.open(in);
			sounds.put(name, clip);
			System.out.println("Sound loaded: " + name);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to load sound: " + name);
		}
	}
	private static void newFont(String name) {
		System.out.println("Loading font: " + name);
		try {
			URL url = Life.class.getResource("fonts/" + name);
			Font font = Font.createFont(Font.TRUETYPE_FONT, url.openStream());
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);
			fonts.put(name, font);
			System.out.println("Font loaded: " + name);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to load font: " + name);
		}
	}
	public static void loadImages() {
        Path path = null;
		try {
        	path = Paths.get(Life.class.getResource("assets").toURI());
			try (Stream<Path> stream = Files.list(path)) {
            stream
                .filter(Files::isRegularFile)
                .forEach(p -> newImage(p.getFileName().toString()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception ignored) {
			System.out.println("Failed to load images.");
			return;
		}
	}
	public static void loadSounds() {
		Path path = null;
		try {
        	path = Paths.get(Life.class.getResource("sounds").toURI());
			try (Stream<Path> stream = Files.list(path)) {
            stream
                .filter(Files::isRegularFile)
                .forEach(p -> newSound(p.getFileName().toString()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception ignored) {
			System.out.println("Failed to load sounds.");
			return;
		}
	}

	public static void loadFonts() {
		Path path = null;
		try {
			path = Paths.get(Life.class.getResource("fonts").toURI());
			try (Stream<Path> stream = Files.list(path)) {
			stream
				.filter(Files::isRegularFile)
				.forEach(p -> newFont(p.getFileName().toString()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception ignored) {
			System.out.println("Failed to load fonts.");
			return;
		}
	}
	
	public static void playSound(String name, int volume) {
		Clip clip = sounds.get(name);
		if (clip == null) { return; }
		soundList.offer(clip);
		volumeList.offer(volume);
	}
	
	public static void playAll() {
		Set<Clip> played = new HashSet<Clip>();
		while (soundList.size() > 0) {
			if (!played.contains(soundList.peek())) {
				play(soundList.peek(), volumeList.poll());
				played.add(soundList.poll());
			} else {
                soundList.poll();
                volumeList.poll();
            }
		}
		played.clear();
	}
	
	public static void play(Clip clip, int volume) {
		clip.stop();
		volume += 2;
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		float range = gainControl.getMaximum() - gainControl.getMinimum();
		float gain = 0;
		if ((range/3 * volume) + gainControl.getMinimum() <= gainControl.getMaximum()) {
			gain = (range/5f * volume) + gainControl.getMinimum();
		} else {
			gain = gainControl.getMaximum();
		}
		gainControl.setValue(gain);
		clip.setFramePosition(0);
		clip.setMicrosecondPosition(0);
		for (int i = 0; !clip.isActive() && i < 100; i ++) {
			clip.start();
		}
	}

}

