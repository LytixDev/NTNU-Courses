#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>

#define XSIZE 2560
#define YSIZE 2048

#define MAXITER 255

int world_size;
int my_rank;

double xleft=-2.01;
double xright=1;
double yupper,ylower;
double ycenter=1e-6;
double step;

int pixel[XSIZE*YSIZE];

#define PIXEL(i,j) ((i)+(j)*XSIZE)

typedef struct {
	double real,imag;
} complex_t;

void calculate() {
	for(int i=0;i<XSIZE;i++) {
		for(int j = my_rank; j < YSIZE; j += world_size) {
			/* Calculate the number of iterations until divergence for each pixel.
			   If divergence never happens, return MAXITER */
			complex_t c,z,temp;
			int iter=0;
			c.real = (xleft + step*i);
			c.imag = (ylower + step*j);
			z = c;
			while(z.real*z.real + z.imag*z.imag < 4) {
				temp.real = z.real*z.real - z.imag*z.imag + c.real;
				temp.imag = 2*z.real*z.imag + c.imag;
				z = temp;
				if(++iter==MAXITER) break;
			}
			pixel[PIXEL(i,j)]=iter;
		}
	}
}

void distribute_results()
{
    if (my_rank == 0) {
        for (int y = 0; y < YSIZE; y++) {
            int sender_rank = y % world_size;
            if (sender_rank != 0) {
                MPI_Recv(&pixel[PIXEL(0, y)], XSIZE, MPI_INT, sender_rank, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
            }
        }
    } else {
        for (int y = my_rank; y < YSIZE; y += world_size) {
            MPI_Send(&pixel[PIXEL(0, y)], XSIZE, MPI_INT, 0, 0, MPI_COMM_WORLD);
        }
    }
}

typedef unsigned char uchar;

/* save 24-bits bmp file, buffer must be in bmp format: upside-down */
void savebmp(char *name,uchar *buffer,int x,int y) {
	FILE *f=fopen(name,"wb");
	if(!f) {
		printf("Error writing image to disk.\n");
		return;
	}
	unsigned int size=x*y*3+54;
	uchar header[54]={'B','M',size&255,(size>>8)&255,(size>>16)&255,size>>24,0,
		0,0,0,54,0,0,0,40,0,0,0,x&255,x>>8,0,0,y&255,y>>8,0,0,1,0,24,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	fwrite(header,1,54,f);
	fwrite(buffer,1,XSIZE*YSIZE*3,f);
	fclose(f);
}

/* given iteration number, set a colour */
void fancycolour(uchar *p,int iter) {
	if(iter==MAXITER);
	else if(iter<8) { p[0]=128+iter*16; p[1]=p[2]=0; }
	else if(iter<24) { p[0]=255; p[1]=p[2]=(iter-8)*16; }
	else if(iter<160) { p[0]=p[1]=255-(iter-24)*2; p[2]=255; }
	else { p[0]=p[1]=(iter-160)*2; p[2]=255-(iter-160)*2; }
}

int main(int argc,char **argv) {
    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &world_size);
    MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);

	/* Calculate the range in the y-axis such that we preserve the
	   aspect ratio */
	step=(xright-xleft)/XSIZE;
	yupper=ycenter+(step*YSIZE)/2;
	ylower=ycenter-(step*YSIZE)/2;


    clock_t start = clock();
	calculate();
    clock_t end_calculate = clock();

    double cpu_time_used = ((double) (end_calculate - start)) / CLOCKS_PER_SEC;
    printf("Calculate took %f seconds for rank %d\n", cpu_time_used, my_rank);

    distribute_results();

    /* Printing of the image */
    if (my_rank == 0) {
        unsigned char *buffer=calloc(XSIZE*YSIZE*3,1);
        for(int i=0;i<XSIZE;i++) {
            for(int j=0;j<YSIZE;j++) {
                int p=((YSIZE-j-1)*XSIZE+i)*3;
                fancycolour(buffer+p,pixel[PIXEL(i,j)]);
            }
        }
        /* write image to disk */
        savebmp("mandel_test.bmp",buffer,XSIZE,YSIZE);
        free(buffer);
    }

    MPI_Finalize();
    clock_t end_final = clock();
    cpu_time_used = ((double) (end_final - start)) / CLOCKS_PER_SEC;
    printf("Everything took %f seconds for rank %d\n", cpu_time_used, my_rank);
	return 0;
}
