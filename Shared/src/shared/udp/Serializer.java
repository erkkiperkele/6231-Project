package shared.udp;

import java.io.*;

public class Serializer<E> {

    public static byte[] serialize(Object object)
            throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(outputStream);
        os.writeObject(object);

        return outputStream.toByteArray();
    }

    @SuppressWarnings("unchecked")
	public E deserialize(byte[] object)
            throws IOException, ClassNotFoundException {

        ByteArrayInputStream in = new ByteArrayInputStream(object);
        ObjectInputStream is = new ObjectInputStream(in);
        return (E) is.readObject();
    }
}
