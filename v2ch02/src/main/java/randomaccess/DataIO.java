package randomaccess;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class DataIO {

    private DataIO() {
    }

    public static String readFixedString(int size, DataInput in) throws IOException {
        var b = new StringBuilder(size);
        var i = 0;
        var done = false;
        while (!done && i < size) {
            var ch = in.readChar();
            i++;
            if (ch == 0) {
                done = true;
            } else {
                b.append(ch);
            }
        }
        in.skipBytes(2 * (size - i));
        return b.toString();
    }

    public static void writeFixedString(String s, int size, DataOutput out) throws IOException {
        for (var i = 0; i < size; i++) {
            var ch = 0;
            if (i < s.length()) {
                ch = s.charAt(i);
            }
            out.writeChar(ch);
        }
    }
}
