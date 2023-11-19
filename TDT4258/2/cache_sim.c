/* PROBLEM: hvordan se forskjell på instruksjon og data i entries i en unified cache */
#include <assert.h>
#include <inttypes.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef enum { dm, fa } cache_map_t;
typedef enum { uc, sc } cache_org_t;
typedef enum { instruction, data } access_t;

typedef struct {
    uint32_t address;
    access_t accesstype;
} mem_access_t;

typedef struct {
    uint64_t accesses;
    uint64_t hits;
    // You can declare additional statistics if
    // you like, however you are now allowed to
    // remove the accesses or hits
} cache_stat_t;

// DECLARE CACHES AND COUNTERS FOR THE STATS HERE

uint32_t cache_size;
uint32_t block_size = 64;
cache_map_t cache_mapping;
cache_org_t cache_org;

// USE THIS FOR YOUR CACHE STATISTICS
cache_stat_t cache_statistics;

/* Reads a memory access from the trace file and returns
 * 1) access type (instruction or data access
 * 2) memory address
 */
mem_access_t read_transaction(FILE *ptr_file)
{
    char type;
    mem_access_t access;

    if (fscanf(ptr_file, "%c %x\n", &type, &access.address) == 2) {
	if (type != 'I' && type != 'D') {
	    printf("Unkown access type\n");
	    exit(0);
	}
	access.accesstype = (type == 'I') ? instruction : data;
	return access;
    }

    /* If there are no more entries in the file,
     * return an address 0 that will terminate the infinite loop in main
     */
    access.address = 0;
    return access;
}

int main(int argc, char **argv)
{
    // Reset statistics:
    memset(&cache_statistics, 0, sizeof(cache_stat_t));

    /* Read command-line parameters and initialize:
     * cache_size, cache_mapping and cache_org variables
     */
    /* IMPORTANT: *IF* YOU ADD COMMAND LINE PARAMETERS (you really don't need to),
     * MAKE SURE TO ADD THEM IN THE END AND CHOOSE SENSIBLE DEFAULTS SUCH THAT WE
     * CAN RUN THE RESULTING BINARY WITHOUT HAVING TO SUPPLY MORE PARAMETERS THAN
     * SPECIFIED IN THE UNMODIFIED FILE (cache_size, cache_mapping and cache_org)
     */
    if (argc != 4) { /* argc should be 2 for correct execution */
	printf("Usage: ./cache_sim [cache size: 128-4096] [cache mapping: dm|fa] "
	       "[cache organization: uc|sc]\n");
	exit(0);
    } else {
	/* argv[0] is program name, parameters start with argv[1] */

	/* Set cache size */
	cache_size = atoi(argv[1]);

	/* Set Cache Mapping */
	if (strcmp(argv[2], "dm") == 0) {
	    cache_mapping = dm;
	} else if (strcmp(argv[2], "fa") == 0) {
	    cache_mapping = fa;
	} else {
	    printf("Unknown cache mapping\n");
	    exit(0);
	}

	/* Set Cache Organization */
	if (strcmp(argv[3], "uc") == 0) {
	    cache_org = uc;
	} else if (strcmp(argv[3], "sc") == 0) {
	    cache_org = sc;
	} else {
	    printf("Unknown cache organization\n");
	    exit(0);
	}
    }

    /* Open the file mem_trace.txt to read memory accesses */
    FILE *ptr_file;
    ptr_file = fopen("mem_trace.txt", "r");
    if (!ptr_file) {
	printf("Unable to open the trace file\n");
	exit(1);
    }

    /* NIC: init params */
    int entries = cache_size / block_size;
    if (cache_org == sc)
	entries = entries / 2;
    assert(entries > 0 && entries <= 64);

    int block_offset_bits = 6; /* NIC: constant because one cache line is always 64 bytes */
    int index_bits;
    if (cache_mapping == fa)
	index_bits = 0;
    else
	index_bits = log2(entries);
    int tag_bits = 32 - index_bits - block_offset_bits;
    int index_mask = (1 << index_bits) - 1;
    assert(tag_bits + 1 <= 32);

    int fa_counter = 0;
    int fa_data_counter = 0;
    int fa_instruction_counter = 0;

    printf("\nCache Organization\n");
    printf("-----------------\n");
    printf("Size:         %d\n", cache_size);
    printf("Block Size:   %d\n", block_size);
    printf("Block Bits:   %d\n", block_offset_bits);
    printf("Index Bits:   %d\n", index_bits);
    printf("Tag Bits:     %d\n", tag_bits);
    printf("Entries:      %d\n", entries);
    printf("-----------------\n\n");

    /*
     * NIC: since we are not storing actual data, just the tag and valid bit,
     *      we get away with using 32 bits for each entry in the cache
     *
     *      I have layed out the cache like this:
     *      First bit is valid bit
     *      Next n bits are for tag where n > 0 and n < 31
     *      So:
     *      0000..0
     *            ^ valid bit
     *      ^^^^^^  tag
     */

    uint32_t cache[entries];
    uint32_t cache_instructions[entries];
    memset(cache, 0, entries * sizeof(cache[0]));
    memset(cache_instructions, 0, entries * sizeof(cache[0]));

    /* Loop until whole trace file has been read */
    mem_access_t access;
    while (1) {
	access = read_transaction(ptr_file);
	// If no transactions left, break out of loop
	if (access.address == 0)
	    break;

	/* NIC: We always access the cache */
	cache_statistics.accesses++;

	int index = (access.address >> block_offset_bits) & (index_mask);
	uint32_t tag = access.address >> (index_bits + block_offset_bits);
	assert(index <= entries);
	//printf("%d %x. ", access.accesstype, access.address);
	//printf("index: %d, tag: %d\n", index, tag);

	uint32_t cache_entry;
	if (cache_mapping == dm) {
	    if (cache_org == sc && access.accesstype == instruction)
		cache_entry = cache_instructions[index];
	    else
		cache_entry = cache[index];
	    /* NIC: check if cache hit: valid and tag is the same */
	    if ((cache_entry & 1) && (cache_entry >> 1) == tag) {
		cache_statistics.hits++;
	    } else {
		/* NIC: evict */
		cache_entry = 1; /* valid bit */
		cache_entry |= (tag << 1); /* tag */
		if (cache_org == sc && access.accesstype == instruction)
		    cache_instructions[index] = cache_entry;
		else
		    cache[index] = cache_entry;
	    }
	} else {
	    uint32_t *cache_to_use = cache;
	    if (cache_org == sc && access.accesstype == instruction)
		cache_to_use = cache_instructions;

	    int broke_out = 0;

	    for (int i = 0; i < entries; i++) {
		cache_entry = cache_to_use[i];
		if ((cache_entry & 1) && (cache_entry >> 1) == tag) {
		    /* NIC: cache hit */
		    cache_statistics.hits++;
		    broke_out = 1;
		    break;
		} else if (!(cache_entry & 1)) {
		    /* NIC: cache miss, but valid bit is 0 so we can insert */
		    cache_entry = 1; /* valid bit */
		    cache_entry |= (tag << 1); /* tag */
		    cache_to_use[i] = cache_entry;
		    broke_out = 1;
		    break;
		}
	    }

	    /* NIC: fa evict */
	    if (!broke_out) {
                int *counter = &fa_counter;
                /* NIC: for split cache set correct counter bassed on accesstype */
                if (cache_org == sc)
                    counter = access.accesstype == instruction ? &fa_instruction_counter : &fa_data_counter;
		cache_entry = 1; /* valid bit */
		cache_entry |= (tag << 1); /* tag */
		cache_to_use[*counter] = cache_entry;

		(*counter)++;
		if (*counter == entries)
		    *counter = 0;
	    }
	}
    }

    /* Print the statistics */
    // DO NOT CHANGE THE FOLLOWING LINES!
    printf("\nCache Statistics\n");
    printf("-----------------\n\n");
    printf("Accesses: %ld\n", cache_statistics.accesses);
    printf("Hits:     %ld\n", cache_statistics.hits);
    printf("Hit Rate: %.4f\n", (double)cache_statistics.hits / cache_statistics.accesses);
    // DO NOT CHANGE UNTIL HERE
    // You can extend the memory statistic printing if you like!

    /* Close the trace file */
    fclose(ptr_file);
}
