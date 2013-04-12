__kernel void my_example(__global char* input, __global char* out) {
	
	
	int number_of_chars=1;
	
	int item_offset = get_global_id(0) * number_of_chars;
	
	for(int i=item_offset; i<item_offset + number_of_chars; i++) {
	
		printf ("Characters: %c %d \n", input[i],get_global_id(0));
	
	}
	
}
