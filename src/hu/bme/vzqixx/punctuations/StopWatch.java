package hu.bme.vzqixx.punctuations;

public class StopWatch {
    protected long elapsed = 0;
    protected boolean isRunning = false;
    protected long lastStarted;

    void reset() {
        isRunning = false;
        elapsed = 0;
    }

    void start() {
        if (!isRunning) {
            isRunning = true;
            lastStarted = System.nanoTime();
        }
    }

    void pause() {
        if (isRunning) {
            elapsed += System.nanoTime()-lastStarted;
            isRunning=false;
        }
    }

    long read() {
        if (isRunning) {
            return elapsed + System.nanoTime() - lastStarted;
        } else {
            return elapsed;
        }
    }
}
