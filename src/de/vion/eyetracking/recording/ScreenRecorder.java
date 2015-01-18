package de.vion.eyetracking.recording;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Handler;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import de.vion.eyetracking.misc.FileManager;
import de.vion.eyetracking.misc.ShellAccess;

/**
 * 
 * The class that handles the recording of the screen
 * 
 * @author André Pomp
 * 
 */
public class ScreenRecorder {

	private static final int TIME_PER_VIDEO = 180000; // Start a new video every
														// 3 minutes
	private static final int POST_PROCESS_START_TIME = 3; // start the post
															// processing after
															// 3 seconds

	private String subDir;

	// Values for the video recordings
	private int counter = 0;
	private List<String> recordedVideoFiles = new ArrayList<String>();

	// Values for the frequent execution
	private final Handler handler = new Handler();
	private Timer timer = new Timer();
	private TimerTask screenRecordTask;

	public ScreenRecorder(String subDir) {
		super();
		this.subDir = subDir;
	}

	public void startRecording() {
		screenRecordTask = new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							String videoFile = FileManager
									.getScreenRecordingFilePath(subDir, counter);
							recordedVideoFiles.add(videoFile);
							ShellAccess.executeCommand(
									"screenrecord --bit-rate 4000000 "
											+ videoFile, false);
							counter++;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		};
		timer.scheduleAtFixedRate(screenRecordTask, 0, TIME_PER_VIDEO);
	}

	public void stopRecording() {
		try {
			timer.cancel(); // stop the timer execution

			String result = ShellAccess.executeCommand("ps screenrecord", true);
			List<Integer> allPIDs = ShellAccess.getPIDsByConsoleString(result);
			for (int pid : allPIDs) {
				// Kill each running screen record process
				ShellAccess.executeCommand("kill -SIGINT " + pid, false);
			}

			final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
					1);
			executor.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						// Read in all movies that we want to merge and store
						// them as
						// movie files
						List<Movie> movieList = new ArrayList<Movie>();
						for (String currentMovie : recordedVideoFiles) {
							File currentMovieFile = new File(currentMovie);
							if (currentMovieFile.exists()) {
								movieList.add(MovieCreator
										.build(currentMovieFile
												.getAbsolutePath()));
							}
						}

						// Create a list of all the video tracks
						// We do not need auto tracks
						List<Track> videoTracks = new LinkedList<Track>();

						for (Movie m : movieList) {
							for (Track t : m.getTracks()) {
								if (t.getHandler().equals("vide")) {
									videoTracks.add(t);
								}
							}
						}

						// Create the final video
						Movie finalVideo = new Movie();
						if (videoTracks.size() > 0) {
							finalVideo.addTrack(new AppendTrack(videoTracks
									.toArray(new Track[videoTracks.size()])));
						}

						// Get the container
						Container container = new DefaultMp4Builder()
								.build(finalVideo);

						// Get the new file
						File myMovie = new File(FileManager
								.getFinalScreenRecordingName(subDir));

						// Store it
						FileOutputStream fos = new FileOutputStream(myMovie);
						FileChannel fco = fos.getChannel();
						fco.position(0);
						for (Box box : container.getBoxes()) {
							box.getBox(fco);
						}
						fco.close();
						fos.close();

						// Delete all the single files
						for (String currentMovie : recordedVideoFiles) {
							File currentMovieFile = new File(currentMovie);
							if (currentMovieFile.exists()) {
								currentMovieFile.delete();
							}
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}, POST_PROCESS_START_TIME, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}