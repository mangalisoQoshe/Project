import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Testing {

	public static void main(String[] args) throws IOException {
		
		Scanner scan = new Scanner(System.in);
		System.out.println("1. Download - dl\n2. Upload - ul\n3. quit - q\nEnter command:");
    	//String message = scan.nextLine();
		System.out.print("> ");
		String message = scan.nextLine();
		System.exit(1);
		
		byte a[] = "Avhusao".getBytes();
		for(int i = 0; i < a.length; i++) {
			System.out.print(a[i]);
		}
		System.out.print("\n");
		byte b[] = "Ramalala".getBytes();
		for(int i = 0; i < b.length; i++) {
			System.out.print(b[i]);
		}
		System.out.print("\n");
		
		Integer I = new Integer(a.length);
		byte w = I.byteValue();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		outputStream.write(w);
		outputStream.write( a );
		outputStream.write( b );

		byte c[] = outputStream.toByteArray( );
		
		for(int i = 0; i < c.length; i++) {
			System.out.print(c[i]);
		}
		System.out.print("\n");
		
		byte[] k = extract(c);
		
		System.out.print("____________________\n");
		
		for(int i = 0; i < k.length; i++) {
			System.out.print(k[i]);
		}
		
	}
	
	public static byte[] extract(byte[] bytes) {
		
		int start = bytes.length-bytes[0]-1;
		
		byte d[] = new byte[start];
		
		int e = 0;
		for(int i = start; i <bytes.length; i++) {
			d[e] = bytes[i];
			e++;
		}
		
		return d;
		
	}

}
