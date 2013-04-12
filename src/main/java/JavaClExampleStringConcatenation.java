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


public class JavaClExampleStringConcatenation {
	public static void main(String[] args) throws IOException {
		CLContext context = JavaCL.createBestContext();
		CLQueue queue = context.createDefaultQueue();
		ByteOrder byteOrder = context.getByteOrder();

		int n = 5;
		ByteBuffer b1 = ByteBuffer.allocateDirect(n);
		ByteBuffer b2 = ByteBuffer.allocateDirect(n);
		Pointer<Byte>
			aPtr = Pointer.pointerToBytes(b1).order(byteOrder),
			bPtr = Pointer.pointerToBytes(b2).order(byteOrder); // allocate an array of float whose length is n

		byte[] bs1="ciaoo".getBytes();
		byte[] bs2="world".getBytes();
		
		for (int i = 0; i < n; i++) { // set the array
			aPtr.set(i,bs1[i]);
			bPtr.set(i, bs2[i]);
		}

		// Create OpenCL input buffers (using the native memory pointers aPtr and bPtr) :
		CLBuffer<Byte> 
			inputBuffer1 = context.createBuffer(Usage.Input, aPtr),
			inputBuffer2 = context.createBuffer(Usage.Input, bPtr);
		
		

		// Create an OpenCL output buffer :
		CLBuffer<Byte> out = context.createByteBuffer(Usage.Output, 12);//Buffer(Usage.Output, n);
		

		// Read the program sources and compile them :
		String src = IOUtils.readText(JavaClExampleStringConcatenation.class.getResource("join.cl"));
		CLProgram program = context.createProgram(src);

		// Get and call the kernel :
		CLKernel addFloatsKernel = program.createKernel("add_floats");
		addFloatsKernel.setArgs(inputBuffer1, inputBuffer2, out, 5,5);
		CLEvent addEvt = addFloatsKernel.enqueueNDRange(queue, new int[] { 5 });

		Pointer<Byte> outPtr = out.read(queue, addEvt); // blocks until add_floats finished

		// Print the first 10 output values :
		byte[] bs=new byte[10];
		for (int i = 0; i < n+n;  i++){
			System.out.println( i+"-"+outPtr.get(i)  );
			bs[i]=outPtr.get(i);
		}
		System.out.println("out[] = " + new String(bs));
			
	}
}
