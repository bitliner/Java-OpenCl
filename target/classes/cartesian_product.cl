__kernel void cartesian_product(__global char* left,__global char* right, __global char* out, int lenL, int lenR) {
	
	
	
	int item_offset = get_global_id(0) * (lenR);
	
	int offset=(lenR+lenL)*get_global_id(0);
	for (int i=0; i<lenL; i++){
		out[offset+i]=left[i];
	}
	
	for(int i=0; i<lenR; i++) {
	
		out[offset+lenL+i]=right[i+item_offset];
	
	
	}
	
}
