import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.bridj.Pointer;

import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLMem.Usage;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.opencl.JavaCL;
import com.nativelibs4java.util.IOUtils;


public class JavaClMYExample {
	public static void main(String[] args) throws IOException {
		CLContext context = JavaCL.createBestContext();
		CLQueue queue = context.createDefaultQueue();
		ByteOrder byteOrder = context.getByteOrder();

		String s="ciao giovanni come stai";
		System.out.println(s.length());
		
		Pointer<Byte> aPtr = Pointer.pointerToBytes( ByteBuffer.allocateDirect(s.length()) ).order(byteOrder);
			// allocate an array of float whose length is n

		byte[] bs1=s.getBytes();
		for (int i = 0; i < s.length(); i++) { // set the array
			aPtr.set(i,bs1[i]);
		}

		// Create OpenCL input buffers (using the native memory pointers aPtr and bPtr) :
		CLBuffer<Byte> inputBuffer1 = context.createBuffer(Usage.Input, aPtr);		

		// Create an OpenCL output buffer :
		CLBuffer<Byte> out = context.createByteBuffer(Usage.Output, s.length() );//Buffer(Usage.Output, n);

		// Read the program sources and compile them :
		String src = IOUtils.readText(JavaClMYExample.class.getResource("my_example.cl"));
		CLProgram program = context.createProgram(src);

		// Get and call the kernel :
		CLKernel addFloatsKernel = program.createKernel("my_example");
		addFloatsKernel.setArgs(inputBuffer1,out);
		CLEvent addEvt = addFloatsKernel.enqueueNDRange(queue, new int[]{23} );

		Pointer<Byte> outPtr = out.read(queue, addEvt); // blocks until add_floats finished

		// Print the first 10 output values :
		byte[] bs=new byte[10];
		for (int i = 0; i < s.length();  i++){
			System.out.println( i+"-"+outPtr.get(i)  );
			bs[i]=outPtr.get(i);
		}
		System.out.println("out[] = " + new String(bs));
			
	}
}
