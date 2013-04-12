import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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


public class bETTERCartesianProductCL {
	public static void main(String[] args) throws IOException {
		CLContext context = JavaCL.createBestContext();
		CLQueue queue = context.createDefaultQueue();
		ByteOrder byteOrder = context.getByteOrder();
		
		Set<String> set1=new HashSet<String>();
		Set<String> set2=new HashSet<String>();
		
		set1.add("111");
		set1.add("222");
		set1.add("333");
		
		set2.add("XXX");
		set2.add("YYY");
		set2.add("ZZZ");
		set2.add("AAA");
		
		Map<Integer,String> index1=createIndex(set1);
		Map<Integer,String> index2=createIndex(set2);
		
		Set<Integer> IDs1=index1.keySet();
		Set<Integer> IDs2=index2.keySet();
		
		System.out.println("size:"+IDs1.size()+","+IDs2.size());
		
		Pointer<Integer> leftPointer = Pointer.allocateInts( IDs1.size() ).order(byteOrder) ;		
		Pointer<Integer> rightPointer =Pointer.allocateInts( IDs2.size() ).order(byteOrder) ;
		
		
		
		Iterator<Integer> it=IDs1.iterator();
		int i=0;
		while (it.hasNext()){
			Integer num=it.next();
			leftPointer.setIntAtIndex(i, num);
//			leftPointer.set(i,num);
			i++;
		}
		
		Iterator<Integer> it2=IDs2.iterator();
		int j=0;
		while (it2.hasNext()){
			Integer num=it2.next();
			rightPointer.set(j,num);
			j++;
		}
		

		CLBuffer<Integer> inputBuffer1 = context.createBuffer(Usage.Input, leftPointer);		
		CLBuffer<Integer> inputBuffer2 = context.createBuffer(Usage.Input, rightPointer);	
		

		
		// Create output buffer :
		int lengthOfBufferOutput=IDs1.size()*IDs2.size();
		CLBuffer<Byte> out = context.createByteBuffer(Usage.Output, lengthOfBufferOutput );

		// Read the program sources and compile them :
		String src = IOUtils.readText(bETTERCartesianProductCL.class.getResource("cartesian_product_2.cl"));
		CLProgram program = context.createProgram(src);

		// Get and call the kernel :
		CLKernel addFloatsKernel = program.createKernel("cartesian_product_2");
		addFloatsKernel.setArgs(inputBuffer1,inputBuffer2,out,IDs1.size(),IDs2.size());
		CLEvent addEvt = addFloatsKernel.enqueueNDRange(queue, new int[]{ Math.max(IDs1.size(),IDs2.size()) } );

		Pointer<Byte> outPtr = out.read(queue, addEvt); // blocks until add_floats finished

		// Print the first 10 output values :
		int[] bs=new int[lengthOfBufferOutput];
		for (i = 0; i < lengthOfBufferOutput;  i++){
			System.out.println( i+"-"+outPtr.get(i)  );
			bs[i]=outPtr.get(i);
		}
		System.out.println("out[] = " + Arrays.toString(bs) );
			
	}
	
	public static <T> Map<Integer,T> createIndex(Set<T> set){
		Map<Integer,T> index=new HashMap<Integer, T>();
		Iterator<T> it=set.iterator();
		int i=0;
		while (it.hasNext()){
			T t=it.next();
			index.put(i, t);
			i++;
		}
		return index;
	}
}
