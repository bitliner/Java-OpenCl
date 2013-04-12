import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

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


public class CartesianProductCL {
	public static void main(String[] args) throws IOException {
		CLContext context = JavaCL.createBestContext();
		CLQueue queue = context.createDefaultQueue();
		ByteOrder byteOrder = context.getByteOrder();

		String left="111";
		int lenghtOfLeft=left.length();
		System.out.println(lenghtOfLeft);
		
		// create buffer for left set
		Pointer<Byte> leftPointer = Pointer.pointerToBytes( ByteBuffer.allocateDirect(lenghtOfLeft) ).order(byteOrder);
		byte[] bs1=left.getBytes();
		for (int i = 0; i < lenghtOfLeft; i++) { // set the array
			leftPointer.set(i,bs1[i]);
		}
		CLBuffer<Byte> inputBuffer1 = context.createBuffer(Usage.Input, leftPointer);		

		// create buffer for right set
		int lengthOfEachRight=3;
		String[] right=new String[]{"XXX","YYY","ZZZ"};
		List<String> rightList=Arrays.asList(right);
		
		Pointer<Byte> rightPointer = Pointer.pointerToBytes( ByteBuffer.allocateDirect( lengthOfEachRight * rightList.size() ) ).order(byteOrder);
		for (int i=0; i<right.length; i++){
			byte[] bs2=right[i].getBytes();
			for (int j = 0; j < lengthOfEachRight; j++) { // set the array
				System.out.println(i+"-"+j);
				rightPointer.set( (i*lengthOfEachRight)+j  ,bs2[j]);
			}
		}
		CLBuffer<Byte> inputBuffer2 = context.createBuffer(Usage.Input, rightPointer);		
		
		
		// Create output buffer :
		int lengthOfBufferOutput=(lenghtOfLeft+lengthOfEachRight)*rightList.size();
		CLBuffer<Byte> out = context.createByteBuffer(Usage.Output, lengthOfBufferOutput );

		// Read the program sources and compile them :
		String src = IOUtils.readText(CartesianProductCL.class.getResource("cartesian_product.cl"));
		CLProgram program = context.createProgram(src);

		// Get and call the kernel :
		CLKernel addFloatsKernel = program.createKernel("cartesian_product");
		addFloatsKernel.setArgs(inputBuffer1,inputBuffer2,out,lenghtOfLeft,lengthOfEachRight);
		CLEvent addEvt = addFloatsKernel.enqueueNDRange(queue, new int[]{rightList.size()} );

		Pointer<Byte> outPtr = out.read(queue, addEvt); // blocks until add_floats finished

		// Print the first 10 output values :
		byte[] bs=new byte[lengthOfBufferOutput];
		for (int i = 0; i < lengthOfBufferOutput;  i++){
			System.out.println( i+"-"+outPtr.get(i)  );
			bs[i]=outPtr.get(i);
		}
		System.out.println("out[] = " + new String(bs));
			
	}
}
