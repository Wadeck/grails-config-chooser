package grails.plugin.configChooser.popup;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class ChoosePopup<T extends IChoiceValue> extends JDialog {
	private static final long serialVersionUID = 1;

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
			secondsBeforeOk = time;
			if (okDefaultAction) {
				timedButton = new OkTimedButton();
			} else {
				timedButton = new CancelTimedButton();
			}
		} else {
			secondsBeforeOk = null;
			timedButton = new NoTimedButton();
		}

		initComponents();
		initFrame();
		initLogic();
		initListeners();
	}

	private void initComponents() {
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		northPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		contentPane.add(northPanel, BorderLayout.NORTH);
		fileChooser = new JComboBox<T>();
		northPanel.add(fileChooser);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		contentPane.add(centerPanel, BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane();
		centerPanel.add(scrollPane);
		fileContent = new JTextPane();
		fileContent.setEditable(false);
		scrollPane.setViewportView(fileContent);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		contentPane.add(southPanel, BorderLayout.SOUTH);
		buttonOK = new JButton("OK");
		buttonCancel = new JButton("Cancel");

		southPanel.add(buttonOK);
		southPanel.add(buttonCancel);

		timedButton.init();
	}

	private void initFrame() {
		setTitle("Choose your configuration source");
		setMinimumSize(new Dimension(200, 250));
		setPreferredSize(new Dimension(400, 400));
		setContentPane(contentPane);
		setAlwaysOnTop(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	private void initLogic() {
		if (secondsBeforeOk == null || secondsBeforeOk <= 0) {
			return;
		}

		countDownTimer = new Timer(1000, new ActionListener() {
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

	private void initListeners() {
		final OnPressedStopCountDownMouseAdapter onPressedStopCountDownMouseAdapter = new OnPressedStopCountDownMouseAdapter();

		// if a single key is pressed, we stop the countdown
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			public boolean dispatchKeyEvent(KeyEvent e) {
				stopCountDown();
				return false;
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		addComponentListener(new OnMoveStopCountDownComponentAdapter());

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		// when user click outside all other component
		contentPane.addMouseListener(onPressedStopCountDownMouseAdapter);

		fileChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileContent.setText(data.getCurrentFileContent());
				stopCountDown();
			}
		});
		fileChooser.addMouseListener(onPressedStopCountDownMouseAdapter);

		fileContent.addMouseListener(onPressedStopCountDownMouseAdapter);

		buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		buttonOK.addMouseListener(onPressedStopCountDownMouseAdapter);

		buttonCancel.addActionListener(new ActionListener() {
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
		stopCountDown();
		closeFrame();
	}

	private void onCancel() {
		stopCountDown();
		cancelCalled = true;
		closeFrame();
	}

	public boolean wasCancelled() {
		return cancelCalled;
	}

	public T getSelectedValue() {
		return data.getSelectedItem();
	}

	public void setData(IChooseData<T> data) {
		this.data = data;
		fileContent.setText(data.getCurrentFileContent());
		fileChooser.setModel(data.getListModel());
		fileChooser.setRenderer(data.getRenderer());
	}

	private void closeFrame() {
		dispose();
	}

	public void askUser() {
		pack();
		setLocationRelativeTo(null);
		timedButton.setDefaultButton();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					setVisible(true);
				}
			});
		} catch (InterruptedException ignored) {
			// do nothing
		} catch (InvocationTargetException ignored) {
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
		public void init() {
			updateTimedButtonText();
			buttonCancel.setText("Cancel");
		}

		public void setDefaultButton() {
			ChoosePopup.this.getRootPane().setDefaultButton(buttonOK);
		}

		public void updateTimedButtonText() {
			String okText;
			if (secondsBeforeOk == null) {
				okText = "OK";
			} else {
				okText = "OK (" + secondsBeforeOk + ")";
			}
			buttonOK.setText(okText);
		}

		public void doClick() {
			buttonOK.doClick();
		}
	}

	private class CancelTimedButton implements TimedButton {
		public void init() {
			updateTimedButtonText();
			buttonOK.setText("OK");
		}

		public void setDefaultButton() {
			ChoosePopup.this.getRootPane().setDefaultButton(buttonCancel);
		}

		public void updateTimedButtonText() {
			String okText = "Cancel";
			if (secondsBeforeOk != null) {
				okText += " (" + secondsBeforeOk + ")";
			}
			buttonCancel.setText(okText);
		}

		public void doClick() {
			buttonCancel.doClick();
		}
	}

	private class NoTimedButton implements TimedButton {
		public void init() {
			buttonOK.setText("OK");
			buttonCancel.setText("Cancel");
		}

		public void setDefaultButton() {
			ChoosePopup.this.getRootPane().setDefaultButton(buttonOK);
		}

		public void updateTimedButtonText() {
		}

		public void doClick() {
		}
	}
}
