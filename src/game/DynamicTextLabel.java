package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class DynamicTextLabel extends JLabel implements Observer {
    DynamicTextModel model;
    public DynamicTextLabel(DynamicTextModel dtm) {
        model = dtm;
        this.setText(model.getText());
        model.addObserver(this);
    }
    public void update(Observable o, Object arg){
        this.setText(model.getText());
    }
}