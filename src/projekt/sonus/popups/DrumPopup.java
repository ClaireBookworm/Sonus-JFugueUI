package projekt.sonus.popups;

import org.jfugue.Player;
import projekt.sonus.sound.Sound;
import projekt.sonus.rectangle.RectangleGrabber;
import projekt.sonus.sound.SoundGrabber;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static javax.swing.SwingConstants.CENTER;
import static javax.swing.SwingConstants.HORIZONTAL;

/**
 * Handles the creation and look of the popup window for the drum.
 * Since the percussion instruments in JFugue is locked to the 9th
 * channel, and the way JFugue reads the percussion sound is much different from the way it reads other instruments
 * its popup window was created in a separate class from the others so that we don't clutter up the InstrumentPopup
 * class.
 */


public class DrumPopup extends JFrame {
    //PANELS
    private JPanel upperPanel, lowerPanel, drumPanel, rightPanel;
    //SOUND
    private final Player player;
    private Sound popupSound;
    private Sound testSound;
    private GridBagConstraints upperC;
    private GridBagLayout lowerPanelLayout;
    private GridBagConstraints lowerC;
    private GridBagLayout popupLayout;
    private GridBagConstraints popupC;
    private GridBagLayout drumLayout;
    private GridBagConstraints drumC;
    private GridBagLayout rightLayout;
    private GridBagConstraints rightC;
    //COLOR
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color BORDER_COLOR = Color.WHITE;
    private Color instrumentColor;
    private static final Color ACTIVE_COLOR = Color.GRAY;
    private static final Color INACTIVE_COLOR = Color.BLACK;
    //DRUMLIST
    private String[] drumList;
    private String[] labels;
    //BUTTON LIST
    private JButton[] drumButtons;
    //DRUM BUTTONS
    private final static int DRUM_SIZE = 10;
    private final static int DRUM_BUTTON_X = 40;
    private final static int DRUM_BUTTON_Y = 40;
    //RIGHT BUTTONS
    private JButton preview, add, clear, combine;
    private final static int RIGHT_BUTTON_X = 22;
    private final static int RIGHT_BUTTON_Y = 22;
    //RIGHT SLIDERS
    private JSlider volume, repeat;
    private int volumeValue, repeatedTimes;
    private final static int VOL_LOW = 0;
    private final static int VOL_MAX = 16000;
    private final static int VOL_INIT = 12000;
    private final static int REP_LOW = 1;
    private final static int REP_MAX = 10;
    private final static int REP_INIT = 1;
    private final static int SLIDER_X = 20;
    private final static int SLIDER_Y = 20;
    //BOTTOM TEXT FIELD
    private final static int LOWER_C_PAD_X = 20;
    private final static int LOWER_C_PAD_Y = 20;
    private final static int TEXT_FIELD_SIZE = 45;
    private JTextField textField;
    //SOUND GRABBER
    private SoundGrabber soundGrabber;
    //RECTANGLE GRABBER
    private RectangleGrabber rectangleGrabber;


    public DrumPopup(SoundGrabber soundGrabber, RectangleGrabber rectangleGrabber, Color color) {
        this.rectangleGrabber = rectangleGrabber;
        this.soundGrabber = soundGrabber;
        instrumentColor = color;
        testSound = new Sound();
        player = new Player();
        popupSound = new Sound();
        panelMaker(getContentPane());
    }

    private void panelMaker(Container pane) {
        popupLayout = new GridBagLayout();
        popupC = new GridBagConstraints();
        pane.setLayout(popupLayout);

        makeUpperPanel();

        makeDrumPanel();
        upperPanel.add(drumPanel, upperC);

        makeRightPanel();
        upperPanel.add(rightPanel, upperC);

        pane.add(upperPanel, popupC);

        makeLowerPanel();
        pane.add(lowerPanel, popupC);

        this.setTitle("Percussion");
        this.setResizable(false);
        pack();
        this.setVisible(true);
    }

    private void makeUpperPanel() {
        upperPanel = new JPanel();
        upperPanel.setBackground(BACKGROUND_COLOR);
        setLayout(popupLayout);
        popupC.ipadx = 0;
        popupC.ipady = 0;
        popupC.gridx = 0;
        popupC.gridy = 0;

        GridBagLayout upperPanelLayout = new GridBagLayout();
        upperC = new GridBagConstraints();
        upperPanel.setLayout(upperPanelLayout);
    }

    private void makeDrumPanel() {

        /**
         * This is the left most panel in the popupwindow.
         */

        Border lineBorder = BorderFactory.createLineBorder(BORDER_COLOR, 2);
        Border drumBorder = BorderFactory.createTitledBorder(lineBorder, "Percussion", 2, 3);

        drumPanel = new JPanel();
        drumPanel.setBorder(drumBorder);
        drumPanel.setBackground(BACKGROUND_COLOR);
        setLayout(popupLayout);
        popupC.ipadx = 0;
        popupC.ipady = 0;
        popupC.gridx = 0;
        popupC.gridy = 0;

        drumLayout = new GridBagLayout();
        drumC = new GridBagConstraints();
        drumPanel.setLayout(drumLayout);

        makeDrumButtons();
        addActionListenersToButtons("drums");
    }

    private void makeDrumButtons() {
        addDrumsToList();
        addLabelsToList();

        drumButtons = new JButton[10];

        int columnIndex = 0;
        int rowIndex = 0;
        for (int i = 0; i < drumList.length; i++) {
            drumButtons[i] = new JButton();
            drumButtons[i].setText(labels[i]);
            drumButtons[i].setForeground(INACTIVE_COLOR);
            setLayout(drumLayout);
            drumC.gridx = columnIndex;
            drumC.gridy = rowIndex;
            drumC.ipadx = DRUM_BUTTON_X;
            drumC.ipady = DRUM_BUTTON_Y;
            drumC.fill = GridBagConstraints.BOTH;
            drumPanel.add(drumButtons[i], drumC);

            /**
             * This adds a focus effect to the button when the mouse howering over the button.
             */
            final int index = i;
            drumButtons[i].addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    drumButtons[index].setForeground(instrumentColor);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    drumButtons[index].setForeground(INACTIVE_COLOR);
                }
            });

            rowIndex = (rowIndex < 4 ? rowIndex + 1 : 0);
            if (rowIndex == 0) {
                columnIndex += 1;
            }

        }

    }

    private void addDrumsToList() {
        /**
         * These are the strings that JFugue reads and converts to sound when the sound is later played.
         */
        drumList = new String[10];
        drumList[0] = "[SPLASH_CYMBAL]q";
        drumList[1] = "[BASS_DRUM]q";
        drumList[2] = "[LOW_TOM]q";
        drumList[3] = "[LOW_MID_TOM]q";
        drumList[4] = "[LOW_FLOOR_TOM]q";

        drumList[5] = "[HAND_CLAP]q";
        drumList[6] = "[CLOSED_HI_HAT]q";
        drumList[7] = "[ELECTRIC_SNARE]q";
        drumList[8] = "[HI_MID_TOM]q";
        drumList[9] = "[HIGH_FLOOR_TOM]q";


    }

    private void addLabelsToList() {
        /**
         * These are the name for the drums. They are shown in the buttons labels and in the textfield when
         * a certain drum is selected and added to the musicstring.
         */
        labels = new String[DRUM_SIZE];
        labels[0] = "Splash Cymbal";
        labels[1] = "Bass Drum";
        labels[2] = "Low Tom";
        labels[3] = "Low Mid Tom";
        labels[4] = "Low Floor Tom";

        labels[5] = "Hand Clap";
        labels[6] = "Closed Hi Hat";
        labels[7] = "Electric Snare";
        labels[8] = "High Mid Tom";
        labels[9] = "High Floor Tom";

    }

    private void makeRightPanel() {

        /**
         * The rightPanel is located in the right most part of the window and houses the sliders, add-, clear-,
         * combine- and preview button.
         */
        Border lineBorder = BorderFactory.createLineBorder(BORDER_COLOR, 2);
        Border rightBorder = BorderFactory.createTitledBorder(lineBorder, "", 2, 3);

        rightPanel = new JPanel();
        rightPanel.setBorder(rightBorder);
        rightPanel.setBackground(BACKGROUND_COLOR);
        popupC.ipadx = 0;
        popupC.ipady = 0;
        popupC.gridx = 1;
        popupC.gridy = 0;

        rightLayout = new GridBagLayout();
        rightC = new GridBagConstraints();
        rightPanel.setLayout(rightLayout);

        makeRightSliders();
        makeRightButtons();

    }

    private void makeRightSliders() {


        String volumeText = "Volume";
        JLabel volumeLabel = new JLabel(volumeText, CENTER);
        volume = new JSlider(HORIZONTAL, VOL_LOW, VOL_MAX, VOL_INIT);
        volume.setMajorTickSpacing(VOL_MAX);
        volume.setMinorTickSpacing(VOL_LOW);

        volumeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        setLayout(rightLayout);
        rightC.gridx = 0;
        rightC.gridy = 0;
        rightC.fill = GridBagConstraints.BOTH;
        rightPanel.add(volumeLabel, rightC);

        volume.setPaintTrack(true);
        setLayout(rightLayout);
        rightC.gridx = 0;
        rightC.gridy = 1;
        rightC.ipadx = SLIDER_X;
        rightC.ipady = SLIDER_Y;
        rightC.fill = GridBagConstraints.BOTH;
        rightPanel.add(volume, rightC);

        String repeatText = "Repeat";
        JLabel repeatLabel = new JLabel(repeatText, CENTER);
        repeat = new JSlider(HORIZONTAL, REP_LOW, REP_MAX, REP_INIT);
        repeat.setMinorTickSpacing(REP_LOW);

        repeatLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        setLayout(rightLayout);
        rightC.gridx = 0;
        rightC.gridy = 2;
        rightC.fill = GridBagConstraints.BOTH;
        rightPanel.add(repeatLabel, rightC);


        repeat.setSnapToTicks(true);
        repeat.setPaintTrack(true);
        repeat.setPaintTicks(true);
        setLayout(rightLayout);
        rightC.gridx = 0;
        rightC.gridy = 3;
        rightC.ipady = SLIDER_Y;
        rightC.ipadx = SLIDER_X;
        rightC.fill = GridBagConstraints.BOTH;
        rightPanel.add(repeat, rightC);


    }

    private void makeRightButtons() {
        String[] buttons = new String[4];
        buttons[0] = "Combine";
        buttons[1] = "Preview";
        buttons[2] = "Add";
        buttons[3] = "Clear";

        combine = new JButton();
        preview = new JButton();
        add = new JButton();
        clear = new JButton();

        final JButton[] rightButtons = new JButton[4];
        rightButtons[0] = combine;
        rightButtons[1] = preview;
        rightButtons[2] = add;
        rightButtons[3] = clear;

        for (int i = 0; i < rightButtons.length; i++) {
            rightButtons[i].setText(buttons[i]);
            rightButtons[i].setForeground(INACTIVE_COLOR);
            setLayout(rightLayout);
            rightC.gridx = 0;
            rightC.gridy = 4 + i;
            rightC.ipadx = RIGHT_BUTTON_X;
            rightC.ipady = RIGHT_BUTTON_Y;
            rightC.fill = GridBagConstraints.BOTH;
            rightPanel.add(rightButtons[i], rightC);

            final int index = i;
            rightButtons[i].addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    rightButtons[index].setForeground(ACTIVE_COLOR);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    rightButtons[index].setForeground(INACTIVE_COLOR);
                }
            });
        }
        addActionListenersToButtons("rightButtons");
    }

    private void makeLowerPanel() {
        /**
         * The lower panel is located at the bottom of the window and houses the textfield which displays the
         * added sound.
         */

        Border lineBorder = BorderFactory.createLineBorder(BORDER_COLOR, 2);
        Border lowerBorder = BorderFactory.createTitledBorder(lineBorder, "", 2, 3);

        lowerPanel = new JPanel();
        lowerPanel.setBackground(BACKGROUND_COLOR);
        lowerPanel.setBorder(lowerBorder);
        setLayout(popupLayout);
        popupC.ipadx = 0;
        popupC.ipady = 0;
        popupC.gridx = 0;
        popupC.gridy = 1;

        lowerPanelLayout = new GridBagLayout();
        lowerC = new GridBagConstraints();
        lowerPanel.setLayout(lowerPanelLayout);

        makeLowerTextArea();
    }

    private void makeLowerTextArea() {
        textField = new JTextField(TEXT_FIELD_SIZE);
        textField.setEditable(false);
        setLayout(lowerPanelLayout);
        lowerC.gridx = 0;
        lowerC.gridy = 0;
        lowerC.ipadx = LOWER_C_PAD_X;
        lowerC.ipady = LOWER_C_PAD_Y;
        lowerC.fill = GridBagConstraints.HORIZONTAL;
        lowerPanel.add(textField, lowerC);
    }


    private void addActionListenersToButtons(String identifier) {

        if (identifier.equals("drums")) {
            setListeners(drumButtons, drumList);

        } else if (identifier.equals("rightButtons")) {
            combine.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    /**
                     * Combine adds a "+" string to the musicstring, which means that the drums selected before and
                     * after the plus sign is played at the same time.
                     */
                    String musicString = popupSound.getMusicString() + "+";
                    popupSound = new Sound();
                    popupSound.add(musicString);
                    textField.setText(textField.getText() + " + ");
                }
            });
            preview.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    popupSound.insertChannel(9);
                    player.play(popupSound);
                }
            });
            clear.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    /**
                     * Simply clears all information.
                     */
                    popupSound = new Sound();
                    textField.setText("");
                }
            });
            add.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    /**
                     * Add uses the repeatedValue and the Sound class addSoundLoop to loop the created musicstring
                     * the selected number of times. After that it inserts the volume value and makes sure
                     * the music is added to the 9th channel, since that channel is reserved for the percussion sound.
                     */
                    volumeValue = volume.getValue();
                    repeatedTimes = repeat.getValue();

                    Sound tempSound = new Sound();
                    tempSound.addSoundLoop(popupSound.getMusicString(), repeatedTimes);
                    popupSound = tempSound;
                    popupSound.insertVolume(volumeValue);
                    popupSound.insertChannel(9);

                    soundGrabber.addToSound(popupSound);
                    rectangleGrabber.setChannelInstrumentTimes(8, "Drums", repeatedTimes);

                    dispose();
                }
            });

        }

    }

    private void setListeners(JButton[] buttonList, String[] musicString) {
        for (int i = 0; i < buttonList.length; i++) {
            final String sound = musicString[i];
            final JButton button = buttonList[i];
            final int index = i;
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    /**
                     * If the combine button is activated we wanted to be able to show the combined drums as:
                     * ' || Drum1 + Drum2 + Drum3 || '
                     */

                    String currentString = popupSound.getMusicString();

                    testSound = new Sound();
                    testSound.addBeatToMusicString(sound);
                    player.play(testSound);
                    if (currentString.endsWith("+")) {
                        String music = popupSound.getMusicString() + sound;
                        popupSound = new Sound();
                        popupSound.add(music);
                        textField.setText(textField.getText() + " " + labels[index]);
                    } else {
                        /**
                         * When a new drum is selected it adds that drum to the musicstring and adds
                         * ' || 'Added Drum' ' to the textfield.
                         */
                        popupSound.add(sound);
                        textField.setText(textField.getText() + " || " + labels[index]);
                    }
                }
            });
        }

    }

}