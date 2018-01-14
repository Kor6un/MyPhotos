import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.concurrent.*;

public class MyFrame extends JFrame implements ActionListener, ComponentListener {

    private static final int THREADS_COUNT = 3;
    private long start,end;
    private JButton grey, negative, blur, outline, clear;
    private ImageIcon originalImageIcon, changedImageIcon;
    private JMenuItem openFile, saveFile, closeFile, exitFile ;
    private JPanel originalImagePanel = new JPanel();
    private JPanel changedImagePanel = new JPanel();
    private JPanel container = new JPanel();
    private JComboBox comboBox;

    private BufferedImage originalImage = null;
    private BufferedImage changedImage = null;

    private int scaleWidth, scaleHeight;
    private JLabel timeLable = new JLabel();
    private JPanel buttomPanel;
    private String time;

    public MyFrame() throws IOException {

        this.setTitle("Мой карманный фотошоп");
        this.setSize (820,390);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.setLayout(new BorderLayout());

        this.addComponentListener(this);

        setLocationRelativeTo(null);

        container.setLayout(new GridLayout(1,2));

        originalImagePanel.setBorder(BorderFactory.createTitledBorder("Оригинал"));
        originalImagePanel.setLayout(new GridLayout(1,1));

        changedImagePanel.setBorder(BorderFactory.createTitledBorder("Измененная"));
        changedImagePanel.setLayout(new GridLayout(1,1));

        container.add(originalImagePanel);
        container.add(changedImagePanel);

        add(this.setMenu(), BorderLayout.NORTH);
        add(container, BorderLayout.CENTER);
        add(this.setButtonPanel(), BorderLayout.EAST);

        this.addComponentListener(null);
        this.setVisible(true);
    }

    private Component setMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu();
        file.setText("Файл");

        openFile = new JMenuItem();
        openFile.setText("Открыть");
        openFile.addActionListener(this);

        saveFile = new JMenuItem();
        saveFile.setText("Сохранить");
        saveFile.addActionListener(this);

        closeFile = new JMenuItem();
        closeFile.setText("Закрыть");
        closeFile.addActionListener(this);

        JSeparator separator = new JSeparator();

        exitFile = new JMenuItem();
        exitFile.setText("Выйти");
        exitFile.addActionListener(this);

        file.add(openFile);
        file.add(saveFile);
        file.add(closeFile);
        file.add(separator);
        file.add(exitFile);

        menuBar.add(file);

        return menuBar;
    }

    private Component setButtonPanel() {

        buttomPanel = new JPanel();
        buttomPanel.setLayout(new GridLayout(10, 1));

        grey = new JButton("Cерый");
        grey.setActionCommand("Grey");
        grey.addActionListener(this);

        negative = new JButton("Негатив");
        negative.setActionCommand("Negative");
        negative.addActionListener(this);

        blur = new JButton("Размытие");
        blur.setActionCommand("Blur");
        blur.addActionListener(this);

        outline = new JButton("Контур");
        outline.setActionCommand("Outline");
        outline.addActionListener(this);

        clear = new JButton("Очистить");
        clear.setActionCommand("Clear");
        clear.addActionListener(this);

        comboBox = new JComboBox();
        comboBox.addItem("Последовательный");
        comboBox.addItem("Параллельный 1");
        comboBox.addItem("Параллельный 2");
        comboBox.addItem("Параллельный 3");


        buttomPanel.add(grey);
        buttomPanel.add(negative);
        buttomPanel.add(blur);
        buttomPanel.add(outline);
        buttomPanel.add(clear);

        buttomPanel.add(comboBox);

        return buttomPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == openFile) {
            try {
                openFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (source == closeFile) {
            closeFile();
        }
        if (source == exitFile) {
            exitFile();
        }
        if (source == saveFile) {
            try {
                saveFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        int result = comboBox.getSelectedIndex();
        String actionCommand = e.getActionCommand();
        switch (actionCommand) {
            case "Grey":
                clearChangedImage();
                try {
                    greyComboBoxChoice(result);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                break;
            case "Negative":
                clearChangedImage();
                negativeComboBoxChoice(result);
                break;
            case "Blur":
                clearChangedImage();
                blurComboBoxChoice(result);
                break;
            case "Outline":
                clearChangedImage();
                outlineComboBoxChoice(result);
                break;
            case "Clear":
                clearChangedImage();
                break;
            default:
                break;
        }
        revalidate();
    }

    private void outlineComboBoxChoice(int result) {
        switch (result) {
            case 0:
                start = System.nanoTime();

                setOutlineConsistent();

                end = System.nanoTime();

                time = ("Время: " + (end - start)/1e9);
                timeLable.setText(time);
                buttomPanel.add(timeLable);
                break;
            case 1:
                start = System.nanoTime();

                try {
                    setOutlineThread();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                end = System.nanoTime();

                time = ("Время: " + (end - start)/1e9);
                timeLable.setText(time);
                buttomPanel.add(timeLable);
                break;
            case 2:
                start = System.nanoTime();

                setOutlineExecutor();

                end = System.nanoTime();

                time = ("Время: " + (end - start)/1e9);
                timeLable.setText(time);
                buttomPanel.add(timeLable);
                break;
            case 3:
                start = System.nanoTime();

                setOutlineFramework();

                end = System.nanoTime();

                time = ("Время: " + (end - start)/1e9);
                timeLable.setText(time);
                buttomPanel.add(timeLable);
                break;
            default:
                break;
        }
    }
    private void blurComboBoxChoice(int result) {
        switch (result) {
            case 0:
                start = System.nanoTime();

                setBlurConsistent();

                end = System.nanoTime();

                time = ("Время: " + (end - start)/1e9);
                timeLable.setText(time);
                buttomPanel.add(timeLable);
                break;
            case 1:
                start = System.nanoTime();

                setBlurThread();

                end = System.nanoTime();

                time = ("Время: " + (end - start)/1e9);
                timeLable.setText(time);
                buttomPanel.add(timeLable);
                break;
            case 2:
                start = System.nanoTime();

                setBlurExecutor();

                end = System.nanoTime();

                time = ("Время: " + (end - start)/1e9);
                timeLable.setText(time);
                buttomPanel.add(timeLable);
                break;
            case 3:
                start = System.nanoTime();

                setBlurFramework();

                end = System.nanoTime();

                time = ("Время: " + (end - start)/1e9);
                timeLable.setText(time);
                buttomPanel.add(timeLable);
                break;
            default:
                break;
        }
    }
    private void negativeComboBoxChoice(int result) {
        switch (result) {
            case 0:
                start = System.nanoTime();

                setNegativeConsistent();

                end = System.nanoTime();

                time = ("Время: " + (end - start)/1e9);
                timeLable.setText(time);
                buttomPanel.add(timeLable);
                break;
            case 1:
                start = System.nanoTime();

                setNegativeThread();

                end = System.nanoTime();

                time = ("Время: " + (end - start)/1e9);
                timeLable.setText(time);
                buttomPanel.add(timeLable);
                break;
            case 2:
                start = System.nanoTime();

                setNegativeExecutor();

                end = System.nanoTime();

                time = ("Время: " + (end - start)/1e9);
                timeLable.setText(time);
                buttomPanel.add(timeLable);
                break;
            case 3:
                start = System.nanoTime();

                setNegativeFramework();

                end = System.nanoTime();

                time = ("Время: " + (end - start)/1e9);
                timeLable.setText(time);
                buttomPanel.add(timeLable);
                break;
            default:
                break;
        }
    }
    private void greyComboBoxChoice(int result) throws InterruptedException {

        switch (result) {
            case 0:
                start = System.nanoTime();

                setGreyConsistent();

                end = System.nanoTime();

                time = ("Время: " + (end - start)/1e9);
                timeLable.setText(time);
                buttomPanel.add(timeLable);
                break;
            case 1:
                start = System.nanoTime();

                setGreyThread();

                end = System.nanoTime();

                time = ("Время: " + (end - start)/1e9);
                timeLable.setText(time);
                buttomPanel.add(timeLable);
                break;
            case 2:
                start = System.nanoTime();

                setGreyExecutor();

                end = System.nanoTime();

                time = ("Время: " + (end - start)/1e9);
                timeLable.setText(time);
                buttomPanel.add(timeLable);
                break;
            case 3:
                start = System.nanoTime();

                setGreyFramework();

                end = System.nanoTime();

                time = ("Время: " + (end - start)/1e9);
                timeLable.setText(time);
                buttomPanel.add(timeLable);
                break;
            default:
                break;
        }
    }

    private void setGreyConsistent() {

        changedImage = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(),
                originalImage.getType());

        Graphics g = changedImage .getGraphics();
        g.drawImage(originalImage, 0, 0, null);

        int height = changedImage.getHeight();
        int width = changedImage.getWidth();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                convertPixelToGrey(changedImage, i, j);
            }
        }
        changedImageIcon = new ImageIcon(changedImage.getScaledInstance(scaleWidth,
                scaleHeight, originalImage.SCALE_SMOOTH));
        changedImagePanel.add(new JLabel(changedImageIcon, SwingConstants.CENTER));
    }
    private void setNegativeConsistent() {

        changedImage = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(),
                originalImage.getType());

        Graphics g = changedImage .getGraphics();
        g.drawImage(originalImage, 0, 0, null);

        int height = changedImage.getHeight();
        int width = changedImage.getWidth();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = changedImage.getRGB(j, i);

                int red = 0xFF - (pixel >> 16) & 0xFF;
                int green = 0xFF - (pixel >> 8) & 0xFF;
                int blue = 0xFF - pixel & 0xFF;

                int newPixel = (0xFF000000 | red << 16 | green << 8 | blue);

                changedImage.setRGB(j, i, newPixel);
            }
        }
        changedImageIcon = new ImageIcon(changedImage.
                getScaledInstance(scaleWidth, scaleHeight, originalImage.SCALE_SMOOTH));
        changedImagePanel.add(new JLabel(changedImageIcon));
    }
    private void setBlurConsistent() {

        changedImage = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(),
                originalImage.getType());

        Graphics g = changedImage .getGraphics();
        g.drawImage(originalImage, 0, 0, null);


        float[] matrix = new float[400];
        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = 1.0f/400.0f;
        }

        BufferedImageOp op = new ConvolveOp( new Kernel(20, 20, matrix),
                ConvolveOp.EDGE_NO_OP, null );
        BufferedImage blurredImage = op.filter(originalImage,changedImage);


        changedImageIcon = new ImageIcon(blurredImage.getScaledInstance(scaleWidth,scaleHeight, originalImage.SCALE_SMOOTH));
        changedImagePanel.add(new JLabel(changedImageIcon));
    }
    private void setOutlineConsistent() {

        changedImage = new BufferedImage(originalImage.getWidth(),
                                         originalImage.getHeight(),
                                         originalImage.getType());
        Graphics g = changedImage .getGraphics();
        g.drawImage(originalImage, 0, 0, null);

        // the sobel matrix in two 2D arrays
        int[][] sx = {{-1,0,1},{-1,0,1},{-1,0,1}};
        int[][] sy = {{-1,-1,-1},{0,0,0},{1,1,1}};

        // get image width and height
        int width = changedImage.getWidth();
        int height = changedImage.getHeight();

        // a sobel template 2D array for calculation
        int[][] sob;
        // at first need to greyscale and populate sob[][] array
        sob = new int[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = changedImage.getRGB(x, y);

                int alpha = (pixel & 0xFF000000) >>> 24;

                int red = (pixel & 0x00FF0000) >>> 16;
                int green = (pixel & 0x0000FF00) >>> 8;
                int blue = (pixel & 0x000000FF);

                int mean = (red + green + blue) / 3;
                sob[x][y] = mean;
                int newPixel = (alpha << 24) + (mean << 16) + (mean << 8) + mean;

                changedImage.setRGB(x, y, newPixel);
            }
        }
        // sobel calculation
        for (int y = 1; y < height-1; y++) {
            for (int x = 1; x < width-1; x++) {
                int px = (sx[0][0] * sob[x-1][y-1]) + (sx[0][1] * sob[x][y-1]) +
                         (sx[0][2] * sob[x+1][y-1]) + (sx[1][0] * sob[x-1][y]) +
                         (sx[1][1] * sob[x][y]) + (sx[1][2] * sob[x+1][y]) +
                         (sx[2][0] * sob[x-1][y+1]) + (sx[2][1] * sob[x][y+1]) +
                         (sx[2][2] * sob[x+1][y+1]);

                int py = (sy[0][0] * sob[x-1][y-1]) + (sy[0][1] * sob[x][y-1]) +
                         (sy[0][2] * sob[x+1][y-1]) + (sy[1][0] * sob[x-1][y]) +
                         (sy[1][1] * sob[x][y]) + (sy[1][2] * sob[x+1][y]) +
                         (sy[2][0] * sob[x-1][y+1]) + (sy[2][1] * sob[x][y+1]) +
                         (sy[2][2] * sob[x+1][y+1]);

                int pixel = (int) Math.sqrt((px * px) + (py * py));

                if (pixel>255) {
                    pixel = 255;
                } else if (pixel<0) {
                    pixel = 0;
                }

                Color pix = new Color(pixel,pixel,pixel);
                changedImage.setRGB(x, y, pix.getRGB());
            }
        }

        changedImageIcon = new ImageIcon(changedImage.getScaledInstance(scaleWidth,scaleHeight, originalImage.SCALE_SMOOTH));
        changedImagePanel.add(new JLabel(changedImageIcon));
    }

    private void convertPixelToGrey(BufferedImage image, int i, int j) {

        int pixel = image.getRGB(j, i);

        int alpha = (pixel & 0xFF000000) >>> 24;

        int red = (pixel & 0x00FF0000) >>> 16;
        int green = (pixel & 0x0000FF00) >>> 8;
        int blue = (pixel & 0x000000FF);

        int mean = (red + green + blue) / 3;
        int newPixel = (alpha << 24) + (mean << 16) + (mean << 8) + mean;

        image.setRGB(j, i, newPixel);
    }

    private void setGreyThread() throws InterruptedException {
        changedImage = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(),
                originalImage.getType());

        Graphics g = changedImage .getGraphics();
        g.drawImage(originalImage, 0, 0, null);
        int width = changedImage.getWidth();
        int height = changedImage.getHeight();

        Thread[] threads = new Thread[THREADS_COUNT];

        for (int k = 0; k < THREADS_COUNT; k++) {
            int n = k;
            threads[k] = new Thread() {
                public void run() {
                    for (int i = n / THREADS_COUNT; i < height * (n+1) / THREADS_COUNT; i++) {
                        for (int j = 0; j < width; j++) {
                            convertPixelToGrey(changedImage, i, j);
                        }
                    }
                }
            };
            threads[k].start();

        }
        for (int i = 0; i < THREADS_COUNT; i++) {
            threads[i].join();
        }

        changedImageIcon = new ImageIcon(changedImage.getScaledInstance(scaleWidth,
                scaleHeight, originalImage.SCALE_SMOOTH));
        changedImagePanel.add(new JLabel(changedImageIcon, SwingConstants.CENTER));

    }
    private void setNegativeThread() {

        changedImagePanel.add(new JLabel("NegativeThread в стадии разработки!"));
    }
    private void setBlurThread() {

        changedImagePanel.add(new JLabel("BlurThread в стадии разработки!"));
    }
    private void setOutlineThread() throws InterruptedException {
        changedImage = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(),
                originalImage.getType());
        Graphics g = changedImage .getGraphics();
        g.drawImage(originalImage, 0, 0, null);

        // the sobel matrix in two 2D arrays
        int[][] sx = {{-1,0,1},{-1,0,1},{-1,0,1}};
        int[][] sy = {{-1,-1,-1},{0,0,0},{1,1,1}};

        // get image width and height
        int width = changedImage.getWidth();
        int height = changedImage.getHeight();
        // a sobel template 2D array for calculation
        int[][] sob;
        // at first need to greyscale and populate sob[][] array
        sob = new int[width][height];

        Thread t1 = new Thread() {
            public void run() {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pixel = changedImage.getRGB(x, y);

                        int alpha = (pixel & 0xFF000000) >>> 24;

                        int red = (pixel & 0x00FF0000) >>> 16;
                        int green = (pixel & 0x0000FF00) >>> 8;
                        int blue = (pixel & 0x000000FF);

                        int mean = (red + green + blue) / 3;
                        sob[x][y] = mean;
                        int newPixel = (alpha << 24) + (mean << 16) + (mean << 8) + mean;

                        changedImage.setRGB(x, y, newPixel);
                    }
                }
            }
        };

        Thread t2 = new Thread() {
            public void run() {
                for (int y = 1; y < height - 1; y++) {
                    for (int x = 1; x < width-1; x++) {
                        int px = (sx[0][0] * sob[x-1][y-1]) + (sx[0][1] * sob[x][y-1]) +
                                (sx[0][2] * sob[x+1][y-1]) + (sx[1][0] * sob[x-1][y]) +
                                (sx[1][1] * sob[x][y]) + (sx[1][2] * sob[x+1][y]) +
                                (sx[2][0] * sob[x-1][y+1]) + (sx[2][1] * sob[x][y+1]) +
                                (sx[2][2] * sob[x+1][y+1]);

                        int py = (sy[0][0] * sob[x-1][y-1]) + (sy[0][1] * sob[x][y-1]) +
                                (sy[0][2] * sob[x+1][y-1]) + (sy[1][0] * sob[x-1][y]) +
                                (sy[1][1] * sob[x][y]) + (sy[1][2] * sob[x+1][y]) +
                                (sy[2][0] * sob[x-1][y+1]) + (sy[2][1] * sob[x][y+1]) +
                                (sy[2][2] * sob[x+1][y+1]);

                        int pixel = (int) Math.sqrt((px * px) + (py * py));

                        if (pixel>255) {
                            pixel = 255;
                        } else if (pixel<0) {
                            pixel = 0;
                        }

                        Color pix = new Color(pixel,pixel,pixel);
                        changedImage.setRGB(x, y, pix.getRGB());
                    }
                }
            }
        };

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        changedImageIcon = new ImageIcon(changedImage.getScaledInstance(scaleWidth,scaleHeight, originalImage.SCALE_SMOOTH));
        changedImagePanel.add(new JLabel(changedImageIcon));

    }

    private void setGreyExecutor() {

        changedImage = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(),
                originalImage.getType());

        Graphics g = changedImage .getGraphics();
        g.drawImage(originalImage, 0, 0, null);

        ExecutorService service = Executors.newFixedThreadPool(THREADS_COUNT);
        service.submit( new ToGrey());
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.shutdown();

        changedImageIcon = new ImageIcon(changedImage.getScaledInstance(scaleWidth,
                scaleHeight, originalImage.SCALE_SMOOTH));
        changedImagePanel.add(new JLabel(changedImageIcon, SwingConstants.CENTER));
    }

    public class ToGrey implements /*Runnable,*/ Callable{

       /* @Override
        public void run() {

            for (int i = 0; i < changedImage.getHeight(); i++) {
                for (int j = 0; j < changedImage.getWidth(); j++) {
                    convertPixelToGrey(changedImage, i, j);
                }
            }
        }*/

        @Override
        public Object call() throws Exception {
            for (int i = 0; i < changedImage.getHeight(); i++) {
                for (int j = 0; j < changedImage.getWidth(); j++) {
                    convertPixelToGrey(changedImage, i, j);
                }
            }
            return changedImage;
        }
    }
    private void setNegativeExecutor() {

        changedImagePanel.add(new JLabel("NegativeExecutor в стадии разработки!", SwingConstants.CENTER));
    }
    private void setBlurExecutor() {

        changedImagePanel.add(new JLabel("BlurExecutor в стадии разработки!", SwingConstants.CENTER));
    }
    private void setOutlineExecutor() {

        changedImagePanel.add(new JLabel("OutlineExecutor в стадии разработки!", SwingConstants.CENTER));
    }

    private void setGreyFramework() {

        changedImagePanel.add(new JLabel("GreyFramework в стадии разработки!", SwingConstants.CENTER));
    }
    private void setNegativeFramework() {

        changedImagePanel.add(new JLabel("NegativeFramework в стадии разработки!", SwingConstants.CENTER));
    }
    private void setBlurFramework() {

        changedImagePanel.add(new JLabel("BlurFramework в стадии разработки!", SwingConstants.CENTER));
    }
    private void setOutlineFramework() {

        changedImagePanel.add(new JLabel("OutlineFramework в стадии разработки!", SwingConstants.CENTER));
    }

    private void clearChangedImage() {
        changedImage = null;
        changedImagePanel.removeAll();
        changedImagePanel.repaint();
    }
    private void clearImagesPanels() {
        originalImage = null;
        changedImage = null;
        originalImagePanel.removeAll();
     //   originalImagePanel.repaint();
        changedImagePanel.removeAll();
     //   changedImagePanel.repaint();
        repaint();
    }

    private void getScaleSize() {
        scaleWidth = originalImagePanel.getWidth() ;
        scaleHeight = originalImagePanel.getHeight()  ;

        if (originalImage.getWidth() >= originalImage.getHeight()) {
            scaleHeight = (scaleWidth * originalImage.getHeight()) / originalImage.getWidth();
        } else  {
            scaleWidth = ((scaleHeight) * originalImage.getWidth()) / originalImage.getHeight();
        }
    }

    private void openFile() throws IOException {
        if (originalImage == null || changedImage == null) {
            openImage();
        } else if (changedImage != null) {

            saveFile();
            openImage();
        }
    }

    private void openImage() throws IOException {
        JFileChooser fileopen = new JFileChooser();
        int ret = fileopen.showDialog(null, "Открыть файл");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileopen.getSelectedFile();
            if (originalImage != null) {
                clearImagesPanels();
            }
            originalImage = ImageIO.read(file);
            getScaleSize();
            originalImageIcon = new ImageIcon(originalImage.getScaledInstance(scaleWidth ,scaleHeight,
                    originalImage.SCALE_SMOOTH));
            originalImagePanel.add(new JLabel(originalImageIcon, SwingConstants.CENTER));
        }
        if (ret == JFileChooser.CANCEL_OPTION) {
            fileopen.cancelSelection();
        }
    }

    private void saveFile() throws IOException {
        JFileChooser saveChooser = new JFileChooser();
        int ret = saveChooser.showSaveDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            if (changedImage != null) {
                File output = new File(saveChooser.getSelectedFile().getAbsolutePath());
                ImageIO.write(changedImage, "jpg",output);
                saveChooser.setSelectedFile(output);
            }
        }
        if (ret == JFileChooser.CANCEL_OPTION) {
            saveChooser.cancelSelection();
        }

    }
    private void closeFile() {
        clearImagesPanels();
    }
    private void exitFile() {
        dispose();
    }

    @Override
    public void componentResized(ComponentEvent e) {

        if (originalImage != null){
            getScaleSize();
            originalImageIcon.setImage(originalImage.getScaledInstance(scaleWidth ,scaleHeight,
                    originalImage.SCALE_SMOOTH));
        }
        if (changedImage != null) {
            changedImageIcon.setImage(changedImage.getScaledInstance(scaleWidth,scaleHeight,
                    changedImage.SCALE_SMOOTH));
        }
        revalidate();
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
