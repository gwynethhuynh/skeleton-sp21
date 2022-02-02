package gh2;
import deque.ArrayDeque;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    public static String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    public static void main(String[] args) {
        ArrayDeque<GuitarString> notes = new ArrayDeque<>();
        for (int i = 0; i < keyboard.length(); i++) {
            double note = 440 * Math.pow(2.0, (i - 24.0) / 12.0);
            notes.addLast(new GuitarString(note));
        }

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (keyboard.contains(String.valueOf(key))) {
                    int index = keyboard.indexOf(key);
                    GuitarString string = notes.get(index);
                    string.pluck();
                }
            }

            /* compute the superposition of samples */
            double sample = 0.0;
            for (int i = 0; i < notes.size(); i++) {
                sample = sample + notes.get(i).sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (int i = 0; i < notes.size(); i++) {
                notes.get(i).tic();
            }
        }
    }

}
