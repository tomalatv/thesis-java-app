import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.media.*;

class TestiIkkuna extends JDialog{
	private static final long serialVersionUID = 1L;
	private JComboBox selectTest;
	private JTextArea testResult;
	private JTextField testRepeat;
	private JFrame saveFile;
	private MultimediaXJava mxj;
	private File fileToOperate;
	private Boolean fileToOperateExist = false;
	private long testTime;

	static final Runtime runtime = Runtime.getRuntime();
	
	private Timer showImageTimer;
	private ActionListener naytaKuva;
	private long kokonais = 0;
	private long aloitus = 0;
	private String aloitusAika;
	private String lopetusAika;
	private int iI;
	private Player player;
	int musToisto=0;
	int repToisto;
	boolean nollaa = false;
		
	public void testSound(){
		aloitus = System.currentTimeMillis();
		mxj.playSong();
	}

	/**
	 * Create the dialog.
	 */
	public TestiIkkuna(MultimediaXJava parent) {
		mxj = parent;
		player = parent.player;
		
		player.addControllerListener(new ControllerListener() {
			
			@Override
			public void controllerUpdate(ControllerEvent arg0) {
				if(player.getState()== Player.Started){
					mxj.stopSong();
					kokonais += System.currentTimeMillis() - aloitus;
					musToisto ++;
					if(musToisto != repToisto){
						testSound();				
					}else{
						lopetusAika = DateFormat.getTimeInstance().format(new Date());
						setTestTime(kokonais);
						showTestResult(aloitusAika, lopetusAika);
						mxj.setTestStatus(false);
					}
				}				
			}
		});
		
		setBounds(100, 100, 238, 343);
		getContentPane().setLayout(null);
		{
			JButton cancelButton = new JButton("Cancel");
			cancelButton.setBounds(10, 239, 82, 23);
			getContentPane().add(cancelButton);
			cancelButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		}
		{
			JButton btnRuntest = new JButton("Run test");
			btnRuntest.setBounds(102, 239, 105, 23);
			getContentPane().add(btnRuntest);
			
			btnRuntest.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {					
					runTesting();
				}
			});
		}
		
		selectTest = new JComboBox();
		selectTest.setBounds(10, 11, 197, 20);
		selectTest.addItem("Avaa tiedostoikkuna");
		selectTest.addItem("Kuvan näyttö");
		selectTest.addItem("Musiikin toisto");
		getContentPane().add(selectTest);
		
		testRepeat = new JTextField();
		testRepeat.setBounds(10, 42, 197, 20);
		getContentPane().add(testRepeat);
		testRepeat.setColumns(10);
		
		testResult = new JTextArea();
		testResult.setBounds(10, 73, 197, 155);
		getContentPane().add(testResult);
		
		JButton btnSaveTestResult = new JButton("Save test result to file");
		btnSaveTestResult.setBounds(10, 273, 164, 23);
		getContentPane().add(btnSaveTestResult);
		
		btnSaveTestResult.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveTestResultToFile();
				
			}
		});
		naytaKuva = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int rep;
				rep = getTestRepeat();
				aloitus = System.currentTimeMillis();
				mxj.showImage();
				kokonais += System.currentTimeMillis() - aloitus;
				iI++;
				if(iI==rep){
					lopetusAika = DateFormat.getTimeInstance().format(new Date());
					showImageTimer.stop();
					setTestTime(kokonais);
					showTestResult(aloitusAika, lopetusAika);
					mxj.setTestStatus(false);
				}
				
			}
		};
		showImageTimer = new Timer(100, naytaKuva);	
	}
	
	private int getSelectedTestIndex(){
		return selectTest.getSelectedIndex();
	}
	
	private String getSelectedTestItem(){
		return selectTest.getSelectedItem().toString();
	}
	
	private int getTestRepeat(){
		return Integer.parseInt(testRepeat.getText());
	}
	
	private void setTestTime(long tTime){
		testTime = tTime;
	}
	
	private String getTestTime(){
		
		double toSec;
		toSec = (double)testTime /1000;
		DecimalFormat df = new DecimalFormat("#0.000");		
		String tt = df.format(toSec);
		return tt;
	}
	
	private void showTestResult(String aloitusAika, String lopetusAika){
		String testResultToFile;
				
		testResult.setText(getSelectedTestItem()+": "+getSelectedTestIndex()
				+"\nAika: "+getTestTime()+" s"
				+"\nAika2: "+testTime +" ms"
				+"\nToistot: "+testRepeat.getText());

		testResultToFile =aloitusAika
				+";"+lopetusAika
				+";"+getSelectedTestIndex()
				+";"+getTestTime()
				+";"+testTime 
				+";"+testRepeat.getText();
		
		if(fileToOperateExist){
			try {
				FileWriter fstream = new FileWriter(fileToOperate,true);
				BufferedWriter out = new BufferedWriter(fstream);
				out.append(testResultToFile);
				out.newLine();
				out.close();
			} catch (IOException e) {
				System.out.println("Tiedostoon kirjoittaminen ei onnistunut");
				e.printStackTrace();
			}
			
		}
	}
	
	private void saveTestResultToFile(){
		saveFile = new JFrame();
		saveFile.setLayout(null);
		saveFile.setSize(600, 450);
		saveFile.setTitle("Save test result");
		
		final JFileChooser fc = new JFileChooser();
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		saveFile.setContentPane(fc);
		saveFile.setVisible(true);
		
		fc.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(e.toString().contains(JFileChooser.CANCEL_SELECTION)){
					saveFile.setVisible(false);
				}else if(e.toString().contains(JFileChooser.APPROVE_SELECTION)){
					
					if(!fc.getSelectedFile().exists()){
						fileToOperate = new File(fc.getSelectedFile().toString()+".txt");
						try {
							fileToOperateExist = fileToOperate.createNewFile();
							FileWriter fstream = new FileWriter(fileToOperate);
							BufferedWriter out = new BufferedWriter(fstream);
							out.write("Aloitus aika | Lopetus aika | Testi nro +" +
									"| Aika s. | Aika ms. | Toistot");
							out.newLine();
							out.close();
						} catch (IOException e1) {
							fileToOperateExist = false;
							e1.printStackTrace();
						}
					}else{
						System.out.println("vanha tiedosto käytössä");
						fileToOperate = fc.getSelectedFile();
						fileToOperateExist = true;
					}
					saveFile.setVisible(false);
				}
			}
		});

	}
		
	private void runTesting() {		
		int sel = 0;
		int rep = 0;
		aloitus = 0;
		kokonais = 0;
		sel = getSelectedTestIndex();
		rep = getTestRepeat();
		mxj.setTestStatus(true);

		aloitusAika = DateFormat.getTimeInstance().format(new Date());
		switch (sel) {
		case 0:
			aloitus = System.currentTimeMillis();
			for (int i = 0; i < rep; i++) {
				 mxj.chooseImages();
				 mxj.fileChooser.setVisible(false);
			}
			kokonais = System.currentTimeMillis() - aloitus;
			lopetusAika = DateFormat.getTimeInstance().format(new Date());
			setTestTime(kokonais);
			showTestResult(aloitusAika, lopetusAika);
			mxj.setTestStatus(false);
			break;

		case 1:
			iI=0;
			showImageTimer.start();	
			break;

		case 2:
			repToisto = rep;
			musToisto =0;
			testSound();
			break;
		}
	}

}
