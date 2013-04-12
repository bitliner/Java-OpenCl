__kernel void cartesian_product_2(__global int* left,__global int* right, __global char* out, int lenL, int lenR) {
	
	
	int item=get_global_id(0);
	
	printf("%d %d %d\n", item, left[item],right[item] );
	
	/*int i;
	for (i=0; i<4; i++){
   		printf("%d %d %d\n", get_global_id(0), left[i], right[i]);
	}*/
	
	
	/*
	out[item]=left[item];
	out[item+1]=left[item+1];
	out[item+2]=right[item];
	out[item+3]=right[item+1];
	*/
	
	
	
}
