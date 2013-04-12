__kernel void add_floats(__global const char* a, __global const char* b, __global char* out, int l1, int l2) 
{
    int i = get_global_id(0);
    
    if (i >= l1)
        return;
        
	printf ("Characters: %c %c \n", a[i], b[i]);
	printf ("Characters: %d %d \n", i, i+l1);
	
    out[i] = a[i];
    out[i+l1] = b[i];
}