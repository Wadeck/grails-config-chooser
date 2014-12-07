package grails.plugin.configChooser.popup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

public class ChoosePopup<T extends IChoiceValue> extends JDialog {
	private JPanel contentPane;
	private JComboBox<T> fileChooser;
	private JTextPane fileContent;
	private JButton buttonOK;
	private JButton buttonCancel;

	private Timer countDownTimer;
	private Integer secondsBeforeOk;
	private TimedButton timedButton;

	private IChooseData<T> data;
	private boolean cancelCalled = false;

	public ChoosePopup(Integer time, boolean okDefaultAction) {
		// this allow the dialog to have a place in the taskbar
		super((Window) null);

		if (time != null) {
			this.secondsBeforeOk = time;
			if (okDefaultAction) {
				this.timedButton = new OkTimedButton();
			} else {
				this.timedButton = new CancelTimedButton();
			}
		} else {
			this.secondsBeforeOk = null;
			this.timedButton = new NoTimedButton();
		}

		this.initComponents();
		this.initFrame();
		this.initLogic();
		this.initListeners();
	}

	private void initComponents() {
		this.contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		northPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		contentPane.add(northPanel, BorderLayout.NORTH);
		{
			this.fileChooser = new JComboBox<T>();
			northPanel.add(fileChooser);
		}

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		contentPane.add(centerPanel, BorderLayout.CENTER);
		{
			JScrollPane scrollPane = new JScrollPane();
			centerPanel.add(scrollPane);
			{
				fileContent = new JTextPane();
				fileContent.setEditable(false);
				scrollPane.setViewportView(fileContent);
			}
		}

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		contentPane.add(southPanel, BorderLayout.SOUTH);
		{
			this.buttonOK = new JButton("OK");
			this.buttonCancel = new JButton("Cancel");

			southPanel.add(buttonOK);
			southPanel.add(buttonCancel);

			timedButton.init();
		}
	}

	private void initFrame() {
		this.setTitle("Choose your configuration source");
		this.setMinimumSize(new Dimension(200, 250));
		this.setPreferredSize(new Dimension(400, 400));
		this.setContentPane(contentPane);
		this.setAlwaysOnTop(true);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		// call onCancel() when cross is clicked
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	private void initLogic() {
		if (secondsBeforeOk != null && secondsBeforeOk > 0) {
			this.countDownTimer = new Timer(1000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					secondsBeforeOk--;
					if (secondsBeforeOk <= 0) {
						secondsBeforeOk = null;
						countDownTimer.stop();
						timedButton.updateTimedButtonText();
						timedButton.doClick();
					} else {
						timedButton.updateTimedButtonText();
					}
				}
			});

			countDownTimer.start();
		}
	}

	private void initListeners() {
		final OnPressedStopCountDownMouseAdapter onPressedStopCountDownMouseAdapter = new OnPressedStopCountDownMouseAdapter();

		// if a single key is pressed, we stop the countdown
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				stopCountDown();
				return false;
			}
		});

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		this.addComponentListener(new OnMoveStopCountDownComponentAdapter());

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		// when user click outside all other component
		contentPane.addMouseListener(onPressedStopCountDownMouseAdapter);

		fileChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileContent.setText(data.getCurrentFileContent());
				stopCountDown();
			}
		});
		fileChooser.addMouseListener(onPressedStopCountDownMouseAdapter);

		fileContent.addMouseListener(onPressedStopCountDownMouseAdapter);

		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		buttonOK.addMouseListener(onPressedStopCountDownMouseAdapter);

		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		buttonCancel.addMouseListener(onPressedStopCountDownMouseAdapter);
	}

	/**
	 * Stop the timer that decrease the time on the ok button
	 */
	private void stopCountDown() {
		countDownTimer.stop();
		secondsBeforeOk = null;

		timedButton.updateTimedButtonText();
	}

	private void onOK() {
		this.stopCountDown();
		closeFrame();
	}

	private void onCancel() {
		this.stopCountDown();
		cancelCalled = true;
		closeFrame();
	}

	public boolean wasCancelled() {
		return cancelCalled;
	}

	public T getSelectedValue() {
		T result = data.getSelectedItem();
		return result;
	}

	public void setData(IChooseData<T> data) {
		this.data = data;
		fileContent.setText(data.getCurrentFileContent());
		fileChooser.setModel(data.getListModel());
		fileChooser.setRenderer(data.getRenderer());
	}

	private void closeFrame() {
		this.dispose();
	}

	public void askUser() {
		this.pack();
		this.setLocationRelativeTo(null);
		timedButton.setDefaultButton();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					setVisible(true);
				}
			});
		} catch (InterruptedException e) {
			// do nothing
		} catch (InvocationTargetException e) {
			// do nothing
		}
	}

	private class OnPressedStopCountDownMouseAdapter extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			stopCountDown();
		}
	}

	private class OnMoveStopCountDownComponentAdapter extends ComponentAdapter {
		private boolean frameVisible = false;

		@Override
		public void componentShown(ComponentEvent e) {
			// last event called after initialization
			frameVisible = true;
		}

		@Override
		public void componentResized(ComponentEvent e) {
			if (frameVisible) {
				stopCountDown();
			}
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			if (frameVisible) {
				stopCountDown();
			}
		}

		@Override
		public void componentHidden(ComponentEvent e) {
			if (frameVisible) {
				stopCountDown();
			}
		}
	}

	private interface TimedButton {
		/**
		 * Initialize the text of the two buttons and place setDefaultButton
		 */
		void init();

		void setDefaultButton();

		void updateTimedButtonText();

		void doClick();
	}

	private class OkTimedButton implements TimedButton {
		@Override
		public void init() {
			updateTimedButtonText();
			buttonCancel.setText("Cancel");
		}

		@Override
		public void setDefaultButton() {
			ChoosePopup.this.getRootPane().setDefaultButton(buttonOK);
		}

		@Override
		public void updateTimedButtonText() {
			String okText;
			if (secondsBeforeOk == null) {
				okText = "OK";
			} else {
				okText = "OK (" + secondsBeforeOk + ")";
			}
			buttonOK.setText(okText);
		}

		@Override
		public void doClick() {
			buttonOK.doClick();
		}
	}

	private class CancelTimedButton implements TimedButton {
		@Override
		public void init() {
			updateTimedButtonText();
			buttonOK.setText("OK");
		}

		@Override
		public void setDefaultButton() {
			ChoosePopup.this.getRootPane().setDefaultButton(buttonCancel);
		}

		@Override
		public void updateTimedButtonText() {
			String okText;
			if (secondsBeforeOk == null) {
				okText = "Cancel";
			} else {
				okText = "Cancel (" + secondsBeforeOk + ")";
			}
			buttonCancel.setText(okText);
		}

		@Override
		public void doClick() {
			buttonCancel.doClick();
		}
	}

	private class NoTimedButton implements TimedButton {
		@Override
		public void init() {
			buttonOK.setText("OK");
			buttonCancel.setText("Cancel");
		}

		@Override
		public void setDefaultButton() {
			ChoosePopup.this.getRootPane().setDefaultButton(buttonOK);
		}

		@Override
		public void updateTimedButtonText() {
		}

		@Override
		public void doClick() {
		}
	}
}
