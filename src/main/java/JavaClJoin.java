import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.bridj.Pointer.allocateFloats;

import java.io.IOException;
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


public class JavaClJoin {
	public static void main(String[] args) throws IOException {
		CLContext context = JavaCL.createBestContext();
		CLQueue queue = context.createDefaultQueue();
		ByteOrder byteOrder = context.getByteOrder();

		int n = 1024;
		Pointer<Float>
			aPtr = allocateFloats(n).order(byteOrder),
			bPtr = allocateFloats(n).order(byteOrder); // alocate an array of float whose length is n

		for (int i = 0; i < n; i++) { // set the array
			aPtr.set(i, (float)cos(i));
			bPtr.set(i, (float)sin(i));
		}

		// Create OpenCL input buffers (using the native memory pointers aPtr and bPtr) :
		CLBuffer<Float> 
		inputBuffer1 = context.createBuffer(Usage.Input, aPtr),
		inputBuffer2 = context.createBuffer(Usage.Input, bPtr);
		
		

		// Create an OpenCL output buffer :
		CLBuffer<Float> out = context.createFloatBuffer(Usage.Output, n);//Buffer(Usage.Output, n);
		

		// Read the program sources and compile them :
		String src = IOUtils.readText(JavaClJoin.class.getResource("kernel.cl"));
		CLProgram program = context.createProgram(src);

		// Get and call the kernel :
		CLKernel addFloatsKernel = program.createKernel("add_floats");
		addFloatsKernel.setArgs(inputBuffer1, inputBuffer2, out, n);
		CLEvent addEvt = addFloatsKernel.enqueueNDRange(queue, new int[] { n });

		Pointer<Float> outPtr = out.read(queue, addEvt); // blocks until add_floats finished

		// Print the first 10 output values :
		for (int i = 0; i < 10 && i < n; i++)
			System.out.println("out[" + i + "] = " + outPtr.get(i));

	}
}
