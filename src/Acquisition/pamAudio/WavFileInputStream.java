package Acquisition.pamAudio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFormat.Encoding;

import sun.security.action.GetLongAction;

import clickDetector.WindowsFile;

import wavFiles.WavHeader;

/**
 * Wav file input stream which will work for large wav files (>2 Gigabytes) which 
 * fail with the standard JAva classes since the data chunk size gets read as a
 * signed integer which then ends up < 0. 
 * <p>
 * This class should work with files of up to 4 Gigabytes (or 2^32 bytes). The only 
 * exception would be mono 8 bit files. This is because various parts of the 
 * super class (e.g. the available() function) 
 * use int for a sample number variable. With stereo or 16 bit files, the 
 * sample number can never exceed 2^31, since will be half or a quarter of the overall
 * file size (minus the header). However, for mono 8 bit files, the sample number can still reach 
 * almost 2^32 which will mess up any parts of the code using a 32 bit sample number. 
 * @author Doug Gillespie
 *
 */
public class WavFileInputStream extends AudioInputStream {

	private WavHeader wavHeader;
	
	private WavFileInputStream(WavHeader wavHeader, InputStream stream, AudioFormat format,
			long length) {
		super(stream, format, length);
		this.wavHeader = wavHeader;
	}

	public static WavFileInputStream openInputStream(File file) throws UnsupportedAudioFileException, IOException
	{
		/*
		 * Read the wav header and from the information in the header, 
		 * create and Audioformat object and also get the file length in frames.
		 */
		WavHeader wavHeader = new WavHeader();
		WindowsFile windowsFile = new WindowsFile(file, "r");
		if (wavHeader.readHeader(windowsFile) == false) {
			throw new UnsupportedAudioFileException("Unsupprted wav file format in " + file.getName());
		}
		long nFrames = wavHeader.getDataSize() / wavHeader.getBlockAlign();
		AudioFormat audioFormat = new AudioFormat(Encoding.PCM_SIGNED, 
				wavHeader.getSampleRate(), wavHeader.getBitsPerSample(), wavHeader.getNChannels(), 
				wavHeader.getBlockAlign(), wavHeader.getSampleRate(), false);
		/**
		 * Close the windows file since it's not compatible with the rest of the audio system 
		 * and pass over a more standard input stream to be read. 
		 */
		windowsFile.close();
		
		BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		inputStream.skip(wavHeader.getDataStart());
		
		return new WavFileInputStream(wavHeader, inputStream, audioFormat, nFrames);
	}

	/**
	 * Get additional header information from the wav file. 
	 * @return the wavHeader
	 */
	public WavHeader getWavHeader() {
		return wavHeader;
	}

}
