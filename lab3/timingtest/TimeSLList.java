package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> ops = new AList<>();
        int M = 1000;

        for (int N = 1000; N < 128001; N = N * 2) {
            SLList s1 = new SLList();
            for (int i = 0; i < N; i++) {
                s1.addLast(1);
            }

            Stopwatch watch = new Stopwatch();
            for (int j = 0; j < M; j++) {
                s1.getLast();
            }
            double ms = watch.elapsedTime();
            Ns.addLast(N);
            times.addLast(ms);
            ops.addLast(M);
        }
        printTimingTable(Ns, times, ops);
    }

}
