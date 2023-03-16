package util;

import view.TopPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;

public class ValueAdjuster extends JComponent {
    //This class represents a JSlider with a corresponding JTextField that follow each other.
    private JSlider slider;
    private JTextField textField;
    private ExtendedNumberFormatter numberFormatter;
    private boolean valueEntered = false;
    private int maximumValue;
    private ChangeListener changeListener;
    private boolean enabled = true;
    private boolean hasValue = false;
    private int lastValue = Integer.MIN_VALUE;

    public ValueAdjuster() {
        this(0, 100);
    }

    public ValueAdjuster(int minSliderValue, int maxSliderValue) {
        ValueAdjuster thisObject = this;
        slider = new JSlider(JSlider.HORIZONTAL, minSliderValue, maxSliderValue, (maxSliderValue-minSliderValue)/2);
        slider.setMinorTickSpacing(100);
        slider.setMajorTickSpacing(100);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                new ChangeEvent(this);
                int value = (int)(slider.getValue() * maximumValue / 100.0);
                if (!valueEntered) {
                    textField.setText("" + value);
                    if (changeListener != null && value != lastValue) {
                        changeListener.stateChanged(new ChangeEvent(thisObject));
                    }
                }
                lastValue = value;
                valueEntered = false;
                repaint();
            }
        });
        add(slider);

        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        numberFormatter = new ExtendedNumberFormatter(format);
        numberFormatter.setMinimum(minSliderValue);
        numberFormatter.setMaximum(Integer.MAX_VALUE);
        numberFormatter.setAllowsInvalid(false);

        textField = new JFormattedTextField(numberFormatter);
        textField.setHorizontalAlignment(JTextField.CENTER);
        add(textField);

        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        int value = Integer.parseInt(textField.getText());
                        valueEntered = true;
                        hasValue = true;
                        slider.setValue((int)(value * 100.0 / maximumValue));
                        slider.setFocusable(true);
                        slider.setEnabled(true);
                        if (changeListener != null) {
                            changeListener.stateChanged(new ChangeEvent(thisObject));
                        }
                        textField.setFocusable(false);
                        textField.setFocusable(true);
                        repaint();
                    } catch (Exception exception) { }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    public void setValue(int value) {
        slider.setValue((int)(value * 100.0 / maximumValue));
        textField.setText(""+value);
        valueEntered = true;
        hasValue = true;
        slider.setFocusable(true);
        slider.setEnabled(true);
    }

    public void removeValue() {
        hasValue = false;
        slider.setFocusable(false);
        slider.setEnabled(false);
        slider.setValue((slider.getMaximum()-slider.getMinimum())/2);
        textField.setText("");
    }

    public boolean hasValue() {
        return hasValue;
    }

    public int getValue() {
        if (!hasValue) {
            return Integer.MIN_VALUE;
        }
        try {
            return Integer.parseInt(textField.getText());
        } catch (NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }

    public void setMaximumValue(int maxValue) {
        maximumValue = maxValue;
        repaint();
    }

    public void addChangeListener(ChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    @Override
    public void setEnabled(boolean enabled) {
        slider.setFocusable(hasValue ? enabled : false);
        slider.setEnabled(hasValue ? enabled : false);
        textField.setFocusable(enabled);
        textField.setEnabled(enabled);
        this.enabled = enabled;
        textField.setText(enabled && hasValue ? "" + getValue() : "");
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        int sliderHeight = getHeight() * 40 / 130;
        slider.setBounds(0, 0, getWidth(), sliderHeight);
        textField.setBounds(0, sliderHeight, getWidth(), getHeight() * 30 / 130);
    }
}
