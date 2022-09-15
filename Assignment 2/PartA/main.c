#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/wait.h>
#include <string.h>

void getProcessData(char *type, int numberOfCalls, int iteration, int level, FILE * stream) {
	char *jsonString;
	if (strcmp(type, "processesArray") == 0) {
		asprintf(&jsonString, "[\n");
	} else {
		numberOfCalls == iteration && level == 1
			? asprintf(&jsonString, "    {\n        \"level\": %d,\n        \"pid\": %d,\n        \"ppid\": %d\n    }\n]", level, getpid(), getppid())
			: asprintf(&jsonString, "    {\n        \"level\": %d,\n        \"pid\": %d,\n        \"ppid\": %d\n    },\n", level, getpid(), getppid());
	}
	fputs(jsonString, stream);
        return;
}

int main() {
        int numberOfCalls;
	char filename[50];
	char extension[] = ".json";
	int level = 0;
        pid_t process_id;

        printf("How many times do you want to call the fork() function?\n>>> ");
        scanf("%d", &numberOfCalls);
	if (numberOfCalls < 0) {
		printf("Error: %d is the incorrect number of calls.", numberOfCalls);
		exit(1);
	}

	printf("Enter a file name where the output will be saved.\n>>> ");
        scanf("%s", filename);

	strcat(filename, extension);
	
	FILE * stream = fopen(filename, "w+");
	if (stream == NULL) {
		printf("ERROR: unable to open file.\n");
		exit(1);
	}

	getProcessData("processesArray", 0, 0, 0, stream);
	fflush(stream);

	getProcessData("processData", numberOfCalls, 0, level, stream);
	fflush(stream);

        for (int i = 1; i <= numberOfCalls; i++) {
		fork() == 0 ? getProcessData("processData", numberOfCalls, i, ++level, stream) : wait(0);
        	fflush(stream);
	}

	fclose(stream);
        return 0;
}
