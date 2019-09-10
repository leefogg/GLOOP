package gloop.logging;

import gloop.graphics.data.models.VertexBuffer;
import gloop.graphics.rendering.texturing.Texture;

import java.io.*;
import java.util.Calendar;
import java.util.TimerTask;

public final class MemoryLogger extends TimerTask {
	private final BufferedWriter logFile;
	public MemoryLogger(String location) throws IOException {
		File file = new File(location);
		file.getParentFile().mkdirs();
		file.createNewFile();
		logFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		logFile.write("Datetime,Total Mesh Bytes,Added Mesh Bytes,Updated Mesh Bytes,,Total Texture Bytes,Texture Bytes Added,Texture Bytes Updated");
		logFile.newLine();
	}

	@Override
	public void run() {
		try {
			Calendar now = Calendar.getInstance();
			logFile.write(""+now.get(Calendar.HOUR_OF_DAY) + ':' + now.get(Calendar.MINUTE) + ':' + now.get(Calendar.SECOND) + ':' + now.get(Calendar.MILLISECOND));
			logFile.write(',');

			logFile.write(Long.toString(VertexBuffer.getTotalBytes()));
			logFile.write(',');
			logFile.write(Long.toString(VertexBuffer.getBytesAdded()));
			logFile.write(',');
			logFile.write(Long.toString(VertexBuffer.getBytesUpdated()));
			logFile.write(',');
			logFile.write(',');

			logFile.write(Long.toString(Texture.getTotalBytes()));
			logFile.write(',');
			logFile.write(Long.toString(Texture.getBytesAdded()));
			logFile.write(',');
			logFile.write(Long.toString(Texture.getBytesUpdated()));

			logFile.newLine();
			//TODO: Add uniforms

			VertexBuffer.clearStatistics();
			Texture.clearStatistics();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void dispose() throws IOException {
		logFile.flush();
		logFile.close();
	}
}
