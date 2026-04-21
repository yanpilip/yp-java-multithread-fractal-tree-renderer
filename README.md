# yp-java-multithread-fractal-tree-renderer
A Java-based graphical application that renders a fractal tree using a recursive branching algorithm distributed across a thread pool. This project demonstrates synchronization patterns, thread-safe data structures, and Swing-based visualization.

🚀 Key Features
- Recursive Fractal Algorithm: Generates a tree structure using trigonometric calculations for branch positioning.

- Multithreaded Execution: Utilizes ExecutorService with a fixed thread pool (128 threads) to compute branch positions concurrently.

- Thread-Safe Producer-Consumer Pattern: Implements a LinkedBlockingQueue to pass line data from worker threads to the UI rendering thread.

- Synchronization & Signaling: Uses wait()/notifyAll() and synchronized blocks to track task completion and manage the application lifecycle.

- Dynamic Visuals: Includes a "Slow Mode" to visualize the tree's growth in real-time, with color differentiation for "leaves" (green) vs. "branches" (black).

🛠️ Technical Deep Dive
# Concurrency Strategy
The project efficiently separates computation from rendering. While worker threads calculate the coordinates ($x, y$) of the branches, the main Canvas thread consumes that data to draw to the screen.
#Synchronization
I implemented a custom task-tracking mechanism using a remTasks counter. This ensures the application knows exactly when the recursive generation is complete, allowing for a clean shutdown of the ExecutorService.

📦 How to Run
1) Clone the repo
2) Compile
3) Run (java FractalTree true)
4) Fastmode Run (java FractalTree false)
