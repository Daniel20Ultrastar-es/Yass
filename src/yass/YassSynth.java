package yass;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/**
 *  Description of the Class
 *
 * @author     Saruta
 * @created    9. Juli 2009
 */
public class YassSynth {
	private static int channels = 1;
	private static int bytesPerSample = 1;
	private static int sampleRate = 44100;// full spectrum to 127 would require 44100

	private static byte[][] data;
	private static AudioFormat audioFormat = null;
	private static AudioFormat wavaudioFormat = null;
	private static SourceDataLine sourceDataLine = null;
	private static SourceDataLine wavsourceDataLine = null;

	private static Vector<Byte> buffer = null;
	private static int BUFFER_SIZE = 128000;

	/**
	 *  Description of the Method
	 *
	 * @param  args  Description of the Parameter
	 */
	public static void main(String args[]) {
		// opening more than 8 clips in parallel fails under Linux/ALSA
		// new approach:
		// open one or more SourceDataLine and write directly, in extra thread
		//
		//Clip clips[] = new Clip[128];

		loadWav();

		long t = 0;

		long s = System.nanoTime() / 1000000L;

		data = new byte[128][];
		for (int i = 0; i < 128; i++) {
			data[i] = create(i, 15, SINE);
			//audioInputStream[i] = cash(data[i], null);
		}
		t = System.nanoTime() / 1000000L - s;
		System.out.println("Sound creation took " + t + "ms.");

		openLine();
		openWavLine();
		t = 0;
		s = System.nanoTime() / 1000000L;
		for (int i = 60; i < 128; i++) {
			t = System.nanoTime() / 1000000L - s;
			System.out.println("n=" + i + " " + getFrequency(i));

			//clips[i].setMicrosecondPosition(0);
			//clips[i].start();

			play(data[i]);
			//playWav();

			try {
				Thread.sleep(100);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		closeLine();
		closeWavLine();
	}


	/**
	 *  Description of the Field
	 */
	public final static int SINE = 0;
	/**
	 *  Description of the Field
	 */
	public final static int RECT = 1;

	// generate signed 16-bit data
	/**
	 *  Description of the Method
	 *
	 * @param  note  Description of the Parameter
	 * @param  ms    Description of the Parameter
	 * @param  type  Description of the Parameter
	 * @return       Description of the Return Value
	 */
	public static byte[] create(int note, int ms, int type) {
		double f = 440 * Math.pow(2, (note - 69) / 12.0);
		byte[] b = new byte[(int) (sampleRate * ms / 1000.0)];
		for (int i = 0; i < b.length; i++) {
			double t = i / (double) sampleRate;
			double sinValue = Math.sin(2 * Math.PI * f * t);
			if (type != SINE) {
				if (type == RECT) {
					sinValue = sinValue > 0 ? 1 : -1;
				}
			}
			float fac = (b.length - i) / (float) b.length;
			b[i] = (byte) Math.min(127, (sinValue * 127.0 * fac * fac));
		}
		/*
		 *  byte data[] = new byte[(int) (sampleRate * bytesPerSample * ms / 1000)];
		 *  ByteBuffer byteBuffer = ByteBuffer.wrap(data);
		 *  ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
		 *  int sampleLength = data.length / bytesPerSample;
		 *  double f = getFrequency(note);
		 *  for (int i = 0; i < sampleLength; i++) {
		 *  double t = i / (double) sampleRate;
		 *  double sinValue = Math.sin(2 * Math.PI * f * t);
		 *  if (type != SINE) {
		 *  if (type == RECT) {
		 *  sinValue = sinValue > 0 ? 1 : -1;
		 *  }
		 *  }
		 *  float fac = (sampleLength - i) / (float) sampleLength;
		 *  shortBuffer.put((short) (32000 * sinValue * fac * fac));
		 *  }
		 */
		return b;
	}


	private static double getFrequency(int midi) {
		return 440 * Math.pow(2, (midi - 69) / 12.0);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  ms  Description of the Parameter
	 * @return     Description of the Return Value
	 */
	public static byte[] createRect(double ms) {
		byte data[] = new byte[(int) (sampleRate * bytesPerSample * ms / 1000)];
		ByteBuffer byteBuffer = ByteBuffer.wrap(data);
		ShortBuffer shortBuffer = byteBuffer.asShortBuffer();

		int sampleLength = data.length / bytesPerSample;
		for (int i = 0; i < sampleLength; i++) {
			double value = 0;
			if (i % 8 < 4) {
				value = 1;
			} else {
				value = -1;
			}
			shortBuffer.put((short) (16000 * value));
		}
		return data;
	}


	/**
	 *  Description of the Method
	 */
	public static void openLine() {
		try {
			if (audioFormat == null) {
				audioFormat = new AudioFormat(sampleRate, bytesPerSample * 8, channels, true, true);
			}
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}


	/**
	 *  Description of the Method
	 */
	public static void openWavLine() {
		try {
			if (audioFormat == null) {
				audioFormat = new AudioFormat(sampleRate, bytesPerSample * 8, channels, true, true);
			}
			if (wavaudioFormat == null) {
				wavaudioFormat = new AudioFormat(sampleRate, 2 * 8, 2, true, true);
			}
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
			wavsourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			wavsourceDataLine.open(wavaudioFormat);
			wavsourceDataLine.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}


	/**
	 *  Description of the Method
	 */
	public static void loadWav() {

		if (audioFormat == null) {
			audioFormat = new AudioFormat(sampleRate, bytesPerSample * 8, channels, true, true);
		}
		if (wavaudioFormat == null) {
			wavaudioFormat = new AudioFormat(sampleRate, 2 * 8, 2, true, true);
		}

		try {
			AudioInputStream ostream = AudioSystem.getAudioInputStream(new BufferedInputStream(YassSynth.class.getClass().getResource("/samples/click.wav").openStream()));
			//System.out.println(ostream.getFormat());
			AudioInputStream stream = AudioSystem.getAudioInputStream(wavaudioFormat, ostream);
			//System.out.println(stream.getFormat());
			long len = stream.getFrameLength() * wavaudioFormat.getFrameSize();
			//stream.read(wavdata, 0, (int) len);

			buffer = new Vector<Byte>((int) len);
			int nBytesRead = 0;
			byte[] abData = new byte[BUFFER_SIZE];
			while (nBytesRead != -1) {
				try {
					nBytesRead = stream.read(abData, 0, abData.length - 1);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				//System.out.println("   Number of bytes read: "+nBytesRead);
				if (nBytesRead >= 0) {
					for (int i = 1; i <= nBytesRead; i++) {
						buffer.add(new Byte(abData[i]));
					}
				}
			}
			stream.close();
			ostream.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  dat  Description of the Parameter
	 */

	/**
	 *  Description of the Method
	 *
	 * @param  dat  Description of the Parameter
	 */
	public static void play(byte dat[]) {
		//Thread playThread = new Thread(new PlayThread());
		//playThread.start();

		//byte tempBuffer[] = new byte[10000];

		try {
			/*
			 *  int cnt;
			 *  while ((cnt =
			 *  audioInputStream[i].read(tempBuffer, 0, tempBuffer.length)) != -1) {
			 *  if (cnt > 0) {
			 *  sourceDataLine.write(tempBuffer, 0, cnt);
			 *  }
			 *  }
			 */
			sourceDataLine.write(dat, 0, dat.length);
			// sourceDataLine.drain();
			//audioInputStream[i].reset();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}


	/**
	 *  Description of the Method
	 */
	public static void closeLine() {
		try {
			sourceDataLine.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 *  Description of the Method
	 */
	public static void closeWavLine() {
		try {
			wavsourceDataLine.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  targetFormat  Description of the Parameter
	 * @param  audioData     Description of the Parameter
	 * @return               Description of the Return Value
	 */
	public static AudioInputStream cash(byte audioData[], AudioFormat targetFormat) {
		/*
		 *  try {
		 *  InputStream bis = new ByteArrayInputStream(data);
		 *  AudioFormat af = new AudioFormat((float) sampleRate, bytesPerSample * 8, channels, true, true);
		 *  AudioInputStream din = new AudioInputStream(bis, af, data.length / af.getFrameSize());
		 *  if (targetFormat != null) {
		 *  din = AudioSystem.getAudioInputStream(targetFormat, din);
		 *  }
		 *  DataLine.Info info = new DataLine.Info(Clip.class, af, ((int) din.getFrameLength() * af.getFrameSize()));
		 *  Clip clip = (Clip) AudioSystem.getLine(info);
		 *  try {
		 *  clip.open(din);
		 *  }
		 *  catch (Exception e) {
		 *  e.printStackTrace();
		 *  return null;
		 *  }
		 *  return clip;
		 *  }
		 *  catch (Exception e) {
		 *  e.printStackTrace();
		 *  }
		 *  return null;
		 */
		try {
			InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
			AudioInputStream stream = new AudioInputStream(byteArrayInputStream, audioFormat, audioData.length / audioFormat.getFrameSize());
			return stream;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	static void monoToStereo(byte[] incoming, byte[] outgoing) {
		for (int i = 0; i < incoming.length; i++) {
			outgoing[(i * 2)] = incoming[i];
			outgoing[(i * 2) + 1] = incoming[i];
		}
	}



	/**
	 *  Gets the frame attribute of the YassSynth class
	 *
	 * @param  frameNum  Description of the Parameter
	 * @return           The frame value
	 */
	public static byte[] getFrame(int frameNum) {
		int frame_size = wavaudioFormat.getFrameSize();

		// To read the 'j'th frame in the file:
		int framePos = (int) ((frameNum - 1) * frame_size);
		byte[] frame = new byte[frame_size];
		Byte sample = null;
		for (int i = 0; i < frame_size; i++) {
			// System.out.println("   -frameNum: "+frameNum+" framePos "+framePos+" i "+i);
			sample = (Byte) buffer.get(framePos + i + 1);
			frame[i] = sample.byteValue();
		}
		return frame;
	}


	/**
	 *  Gets the lengthInFrames attribute of the YassSynth class
	 *
	 * @return    The lengthInFrames value
	 */
	public static int getLengthInFrames() {
		int frame_length = audioFormat.getFrameSize();
		return (int) (buffer.size() / frame_length) - 1;
	}


	/**
	 *  Description of the Method
	 */
	private static byte[] tempBuff = new byte[BUFFER_SIZE];


	/**
	 *  Description of the Method
	 */
	public static void playWav() {
		//Thread t =
		//	new Thread() {
		//		public void run() {
		double snd_pos = 1;// In Frames
		int frame_length = wavaudioFormat.getFrameSize();
		int length_in_frames = (int) (buffer.size() / frame_length) - 1;
		//System.out.println("   -frame length " + frame_length + " length_in_frames " + length_in_frames);

		byte[] sample = null;

		wavsourceDataLine.flush();

		while (Math.floor(snd_pos) <= length_in_frames) {
			// System.out.println("   -Filling a buffer");

			int buff_pos = 0;
			while ((buff_pos < BUFFER_SIZE) & (Math.floor(snd_pos) <= length_in_frames)) {
				sample = getFrame((int) Math.floor(snd_pos));
				for (int i = 0; i < frame_length; i++) {
					tempBuff[buff_pos++] = sample[i];
				}
				snd_pos++;
			}

			if (buff_pos > 0) {
				//System.out.println("   -Doing a write: "+buff_pos);
				int nBytesWritten = wavsourceDataLine.write(tempBuff, 0, buff_pos);
				//for (int i=0; i<tempBuff.length; i++)
				//	System.out.print(tempBuff[i]+", ");
				//System.out.println("---"+tempBuff.length+"---");
			}
		}
		//		}
		//	};
		//t.start();
	}

}
