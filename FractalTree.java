import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class FractalTree extends Canvas {

    private static final Object COUNTER_LOCK = new Object();

    private static boolean slowMode;

    private static final BlockingQueue<LineCont> lineQueue = new LinkedBlockingQueue<LineCont>(5000);
    private static ExecutorService executor;

    private static int remTasks;

    private static void sumbitBr(final int x, final int y, final int angle, final int height) {
        Runnable work = new Runnable() {
            @Override
            public void run() {
                try {
                    computeBr(x, y, angle, height);
                } 
                finally {
                    markdone();
                }
            }
        };

        synchronized (COUNTER_LOCK) {
            remTasks = remTasks + 1;
        }

        executor.submit(work);
    }

    private static void computeBr(int x, int y, int angle, int height) {
        if (slowMode) {
            try {
                Thread.sleep(100);
            } 
            catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
        }

        if (height == 0) {
            return;
        }

        int x2 = x + (int) (Math.cos(Math.toRadians(angle)) * height * 8);
        int y2 = y + (int) (Math.sin(Math.toRadians(angle)) * height * 8);
        Color lineColor;

        if (height < 5) {
            lineColor = Color.GREEN;
        } 
        else {
            lineColor = Color.BLACK;
        }

        LineCont line = new LineCont(x, x2, y, y2, lineColor);
        boolean added = false;

        while (!added) {
            try {
                lineQueue.put(line);
                added = true;
            } 
            catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
        }

        int nextHeight = height - 1;

        if (nextHeight >= 0) {
            sumbitBr(x2, y2, angle - 20, nextHeight);
            computeBr(x2, y2, angle + 20, nextHeight);
        }
    }

    private static void markdone() {
        synchronized (COUNTER_LOCK) {
            remTasks = remTasks - 1;
            COUNTER_LOCK.notifyAll();
        }
    }

    private static void waitTasks() {
        synchronized (COUNTER_LOCK) {
            while (remTasks > 0) {
                try {
                    COUNTER_LOCK.wait();
                } 
                catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        while (true) {
            try {
                LineCont line = lineQueue.take();
                g.setColor(line.color);
                g.drawLine(line.x1, line.y1, line.x2, line.y2);
            } 
            catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public static void main(String args[]) {
        if (args.length == 0) {
            slowMode = true;
        } 
        else {
            slowMode = Boolean.parseBoolean(args[0]);
        }

        executor = Executors.newFixedThreadPool(128);

        FractalTree tree = new FractalTree();
        JFrame frame = new JFrame();
        frame.setSize(800, 600);
        frame.add(tree);
        frame.setVisible(true);

        sumbitBr(390, 480, -90, 10);
        waitTasks();
        executor.shutdown();
        System.out.println("Main has finished");
    }
}
