package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import javax.imageio.plugins.tiff.TIFFDirectory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.awt.Point;

public class MapPanel extends JPanel {
    public MapPanel() throws IOException {
        JPanel panel = new JPanel();
        GameScreen screen = new GameScreen();
        panel.setLayout(new GridLayout(1, 1));
        panel.setPreferredSize(new Dimension(screen.width, screen.height));
        panel.add(screen);
        this.add(panel, BorderLayout.CENTER);
    }
}
