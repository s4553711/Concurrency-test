### Prep
Create test files
```bash=
$ pigz -dck NA12878D_HiSeqX_R1_.fastq.gz | head -n 40000000 | ~/tool/lz4-1.8.1.2/lz4 > a.fastq.gz
$ ~/tool/lz4-1.8.1.2/lz4 -dc a.fastq.gz | wc --bytes
3,565,445,699
```

### Benchmark
lz4 decompression only, 869 MB/s
```bash=
$ /usr/bin/time -v ~/tool/lz4-1.8.1.2/lz4 -dc a.fastq.gz > /dev/null
Elapsed (wall clock) time (h:mm:ss or m:ss): 0:03.91
```

lz4 decompression and poll, 318 MB/s
```bash=
$ /usr/bin/time -v ~/tool/lz4-1.8.1.2/lz4 -dc a.fastq.gz | java -jar build/libs/LAMXTest-dev-0.1.jar 1>/dev/null
Dif: 10690
```

lz4 decompression, poll and iterator byte array, 334 MB/s
```bash
~/tool/lz4-1.8.1.2/lz4 -dc a.fastq.gz | java -jar build/libs/LAMXTest-dev-0.1.jar 1>/dev/null
Dif: 10152
```