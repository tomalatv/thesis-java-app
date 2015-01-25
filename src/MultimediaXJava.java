import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.media.CannotRealizeException;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.Timer;

public class MultimediaXJava {

	private static MultimediaXJava window;
	
	private final String OPEN_AUDIO_DIR = "C:\\";
	private final String OPEN_IMG_DIR = "C:\\";

	private JFrame frame;
	private TestiIkkuna testiIkkunaDialog;
	private JButton btnTest;
	private JButton btnOpenPics;
	private JButton btnOpenFile;
	private JButton btnPlay;
	private JButton btnStop;
	private JTextArea songInfo;
	private JLabel songName;
	private JLabel imgLabel;
	private JLabel songPercent;
	private JProgressBar songProgressBar;
	protected JFrame fileChooser;
	protected JFileChooser fc;
	private Boolean test = false;
	private ImageIcon imgIcon;
	private BufferedImage imgScaled;
	private int imageCounter = 0;
	private int imageDelay = 3000;
	private int progressBarDelay = 500;
	private Timer imageTimer;
	private Timer progressBarTimer;
	private File audioFile = null;
	private File imgFileRoot;
	private File audioFileRoot;
	private File imageFiles[] = null;
	protected Player player = null;
	private FileFilter imgFilter;
	private FileFilter audioFilter;
	private int fileChooserMode = 0;
	private int playMode = 0;
	
	private MediaLocator mlr = null;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new MultimediaXJava();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the application.
	 */
	public MultimediaXJava() {
		initialize();
		imgIcon = new ImageIcon();
		imgScaled = new BufferedImage(imgLabel.getWidth(),
				imgLabel.getHeight(), BufferedImage.TYPE_INT_RGB);
		imageTimer = new Timer(imageDelay, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showImage();
				
			}
		});
		progressBarTimer = new Timer(progressBarDelay, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				updateSongInformation();
				
			}
		});

		imgFileRoot = new File(OPEN_IMG_DIR);
		audioFileRoot = new File(OPEN_AUDIO_DIR);
	
		fileChooser = new JFrame();
		fileChooser.setLayout(null);
		fileChooser.setSize(600, 450);
		
		fc = new JFileChooser();
		fc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.toString().contains(JFileChooser.CANCEL_SELECTION)) {
					fileChooser.setVisible(false);
				} else if (e.toString().contains(JFileChooser.APPROVE_SELECTION)) {
					if (fileChooserMode == 0 && fc.getSelectedFiles().length != 0 && !test ) {
						imageFiles = fc.getSelectedFiles();						
					}else if (fileChooserMode == 1 && fc.getSelectedFile().isFile() && !test) {
						audioFile = fc.getSelectedFile();
						try {
							mlr = new MediaLocator(audioFile.toURI().toURL());
							player = Manager.createRealizedPlayer(mlr);
						} catch (NoPlayerException e1) {
							System.out.println(e1.toString());
							e1.printStackTrace();
						} catch (CannotRealizeException e1){
							System.out.println(e1.toString());
							e1.printStackTrace();
						}catch (MalformedURLException e1) {
							System.out.println(e1.toString());
							e1.printStackTrace();
						} catch (IOException e1) {
							System.out.println(e1.toString());
							e1.printStackTrace();
						}
						songName.setText(fc.getName(audioFile));
					}
					fileChooser.setVisible(false);
				}
			}
		});
		
		
		imgFilter = new FileFilter() {
			
			@Override
			public String getDescription() {
				return "jpg, jpeg";
			}
			
			@Override
			public boolean accept(File f) {
				return f.isDirectory()
				|| f.getName().toLowerCase().endsWith(".jpg")
				|| f.getName().toLowerCase().endsWith(".jpeg");
			}
			
		};
		
		audioFilter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory()
				|| f.getName().toLowerCase().endsWith(".mp3");
			}

			@Override
			public String getDescription() {
				return "mp3";
			}
		};
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {		
		frame = new JFrame("MultimediaXJava");
		frame.setBounds(100, 100, 352, 491);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		btnOpenPics = new JButton("Open pics");
		btnOpenPics.setBounds(10, 11, 104, 23);
		frame.getContentPane().add(btnOpenPics);
		btnOpenPics.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseImages();
				
			}
		});
		
		btnTest = new JButton("Test");
		btnTest.setBounds(124, 11, 89, 23);
		frame.getContentPane().add(btnTest);
		btnTest.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				testiIkkunaDialog = new TestiIkkuna(window);
				testiIkkunaDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				testiIkkunaDialog.setVisible(true);				
			}
		});
		
		imgLabel = new JLabel("");
		imgLabel.setBounds(10, 45, 320, 220);
		frame.getContentPane().add(imgLabel);
		
		songProgressBar = new JProgressBar();
		songProgressBar.setBounds(10, 276, 267, 14);
		frame.getContentPane().add(songProgressBar);
		
		songPercent = new JLabel("");
		songPercent.setBounds(284, 276, 30, 14);
		frame.getContentPane().add(songPercent);
		
		songName = new JLabel("");
		songName.setBounds(10, 301, 320, 14);
		frame.getContentPane().add(songName);
		
		btnOpenFile = new JButton("Open file");
		btnOpenFile.setBounds(10, 326, 89, 23);
		frame.getContentPane().add(btnOpenFile);
		btnOpenFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseSongToPlay();
				
			}
		});
		
		btnPlay = new JButton("Play");
		btnPlay.setBounds(126, 326, 89, 23);
		frame.getContentPane().add(btnPlay);
		btnPlay.setActionCommand("Play");
		btnPlay.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("Play")){
					playMode = 0;
					playSong();
				}else if(e.getActionCommand().equals("Pause")){
					playMode = 1;
					playSong();
				}
			}
		});
		
		btnStop = new JButton("Stop");
		btnStop.setBounds(237, 326, 89, 23);
		frame.getContentPane().add(btnStop);
		btnStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				stopSong();
				
			}
		});
		
		songInfo = new JTextArea();
		songInfo.setBounds(10, 360, 316, 82);
		frame.getContentPane().add(songInfo);
	}

	/**
	 * Asettaa testi statuksen
	 * @param testStatus
	 */
	protected void setTestStatus(boolean testStatus){
		test = testStatus;
	}
	
	/**
	 * Audiotiedoston toisto
	 */
	protected void playSong() {
		if (audioFile != null && player != null) {
			switch (playMode){
			case 1:
				player.stop();
				imageTimer.stop();
				progressBarTimer.stop();
				btnPlay.setText("Play");
				btnPlay.setActionCommand("Play");
				break;

			default:
				player.start();
				if (!test) {
					imageTimer.start();
					progressBarTimer.start();
					btnPlay.setText("Pause");
					btnPlay.setActionCommand("Pause");
				}
				break;
			}
		}
	}
	
	/**
	 * pys‰ytt‰‰ audiotiedoston toistaminsen
	 */
	protected void stopSong() {

		if (player != null){
			player.stop();
			player.deallocate();
			if (!test) {
				imageTimer.stop();
				progressBarTimer.stop();
				btnPlay.setText("Play");
				btnPlay.setActionCommand("Play");
			}
		}
	}	

	/**
	 * P‰ivitt‰‰ audiotiedoston toistoon liittyvi‰ tietoja sovelluksen
	 * k‰yttˆliittym‰‰n
	 */
	private void updateSongInformation() {
		double lenght=0;
		double elapse=0;
		double dProcents;
		int iPercents;

		lenght = player.getDuration().getSeconds();
		elapse = player.getMediaTime().getSeconds();

		dProcents = (elapse / lenght) * 100;
		iPercents = (int) dProcents;
		songProgressBar.setValue(iPercents);
		songPercent.setText(iPercents + "%");

		lenght = lenght / 60;
		elapse = elapse / 60;

		DecimalFormat df = new DecimalFormat("#0.00");
		String aik = df.format(elapse);
		String pit = df.format(lenght);
		String soi = df.format(iPercents);

		songInfo.setText("Aika: " + aik + "\nPituus: " + pit
				+ "\nBiisi: " + audioFile.getName() + "\nSoitto: " + soi + "%");

	}

	/**
	 * N‰ytt‰‰ valitut kuvat 
	 */
	protected void showImage() {
		if (imageFiles != null) {
			try {
				if (imageCounter == imageFiles.length) {
					imageCounter = 0;
				}
				BufferedImage imgSrc = ImageIO.read(imageFiles[imageCounter]);
				Graphics2D g = imgScaled.createGraphics();
				g.drawImage(imgSrc, 0, 0, imgLabel.getWidth(), imgLabel.getHeight(), null);
				g.dispose();
				imgIcon.setImage(imgScaled);
				imgLabel.setIcon(imgIcon);
				imgLabel.update(imgLabel.getGraphics());
				imageCounter++;
			} catch (IOException e) {
				System.out.println(e.toString());
				e.printStackTrace();
			}
		}

	}

	/**
	 * Avaa tiedostovalintaikkunan kuvien valintaan varten
	 */
	protected void chooseImages() {
		fileChooserMode =0;
		fileChooser.setTitle("Open picture");
		fc.setMultiSelectionEnabled(true);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.addChoosableFileFilter(imgFilter);
		fc.setFileFilter(imgFilter);
		fc.setCurrentDirectory(imgFileRoot);
		fileChooser.setContentPane(fc);
		fileChooser.setVisible(true);
	}
	
	/**
	 * Avaa tiedostovalintaikkunan audio tiedoston valintaa varten
	 */
	protected void chooseSongToPlay() {
		fileChooserMode =1;
		fileChooser.setTitle("Select audio file to play");
		fc.setMultiSelectionEnabled(false);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.addChoosableFileFilter(audioFilter);
		fc.setFileFilter(audioFilter);
		fc.setCurrentDirectory(audioFileRoot);
		fileChooser.setContentPane(fc);
		fileChooser.setVisible(true);
	}

}