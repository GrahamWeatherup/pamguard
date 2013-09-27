package pamScrollSystem;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.sun.media.sound.EmergencySoundbank;

import PamView.PamDialog;
import PamView.PamGridBagContraints;

public class LoadingDataDialog extends PamDialog {
	
	private JProgressBar streamProgress;
	
	private JProgressBar allProgress;
	
	private JLabel storeName, streamName;

	private boolean emergencyStop;
	
	private static LoadingDataDialog loadingDataDialog;

	private LoadingDataDialog(Window parentFrame) {
//		super();
		super(parentFrame, "Loading viewer data", false);
		streamProgress = new JProgressBar(SwingConstants.HORIZONTAL);
		allProgress = new JProgressBar(SwingConstants.HORIZONTAL);
		storeName = new JLabel("    ", JLabel.LEFT);
		streamName = new JLabel("   ", JLabel.LEFT);
//		storeName.setEditable(false);
//		streamName.setEditable(false);
		JPanel p = new JPanel();
		p.setBorder(new TitledBorder("Data load progress ..."));
		p.setLayout(new GridBagLayout());
		GridBagConstraints c = new PamGridBagContraints();
		addComponent(p, storeName, c);
		c.gridy++;
		addComponent(p, allProgress, c);
		c.gridy++;
		addComponent(p, streamName, c);
		c.gridy++;
		addComponent(p, streamProgress, c);
		c.gridy++;
//		p.add(storeName);
//		p.add(allProgress);
//		p.add(streamName);
//		p.add(streamProgress);
		JPanel stopPanel = new JPanel(new BorderLayout());
		JButton stopButton = new JButton("Stop data load");
		stopPanel.add(BorderLayout.EAST, stopButton);
		stopButton.addActionListener(new StopButton());
		addComponent(p, stopPanel, c);
		
		storeName.setPreferredSize(new Dimension(250, 0));
		
		streamProgress.setIndeterminate(true);
		streamProgress.setMaximum(100);
		
		setDialogComponent(p);
//		add(p);
//		pack();

		getOkButton().setVisible(false);
		getCancelButton().setVisible(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setModalityType(Dialog.ModalityType.MODELESS);
	}
	
	public static LoadingDataDialog showDialog(Window frame) {
		if (loadingDataDialog == null || loadingDataDialog.getOwner() == null ||
				loadingDataDialog.getOwner() != frame) {
			loadingDataDialog = new LoadingDataDialog(frame);
		}
		loadingDataDialog.emergencyStop = false;
		loadingDataDialog.setVisible(true);
		return loadingDataDialog;
	}
	
	@Override
	public void setVisible(boolean visible) {
		/**
		 * reschedule closing to happen on the Swing thread. 
		 * I've no idea why this happens, but if I don't then 
		 * the main frame ends up being pushed back down a layer
		 * and disappears behind another window. 
		 */
		if (visible == false) {
			closeLater();
		}
		else {
			super.setVisible(visible);
		}
	}
	

	public void setData(LoadQueueProgressData progressData) {
		if (progressData.getStreamName() != null) {
			allProgress.setMaximum(progressData.getTotalStreams());
			allProgress.setValue(progressData.getIStream());
//			storeName.setText(progressData.getStoreType());
			storeName.setText(String.format("Loading data block %d of %d", 
					progressData.getIStream(), progressData.getTotalStreams()));
			streamName.setText(progressData.getStreamName());
		}
		long interval = progressData.getLoadEnd() - progressData.getLoadStart();
		if (interval == 0) {
			streamProgress.setIndeterminate(true);
		}
		else {
			streamProgress.setIndeterminate(false);
			long done = progressData.getLoadCurrent() - progressData.getLoadStart();
			int percent = (int) (100L*done / interval);
			streamProgress.setValue(percent);
		}
			
	}


	@Override
	public void cancelButtonPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getParams() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void restoreDefaultSettings() {
		// TODO Auto-generated method stub

	}

	class StopButton implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			stopPressed();
		}
		
	}
	
	void stopPressed() {
		int ans = JOptionPane.showConfirmDialog(this, "Do you want to stop loading data ?",
				"Data Loading", JOptionPane.YES_NO_OPTION);
		if (ans == JOptionPane.YES_OPTION) {
			emergencyStop = true;
		}
	}
	
	public boolean shouldStop() {
		return emergencyStop;
	}

}
