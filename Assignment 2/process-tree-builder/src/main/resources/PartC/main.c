#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/wait.h>
#include <string.h>

void getProcessData(char *type, int level, FILE * stream) {
	char *jsonString;
	if (strcmp(type, "processesArray") == 0) {
		asprintf(&jsonString, "[\n");
	} else if (strcmp(type, "lastProcess") == 0) {
		asprintf(&jsonString, "    {\n        \"level\": %d,\n        \"pid\": %d,\n        \"ppid\": %d\n    }\n]", level, getpid(), getppid());
	} else {
		asprintf(&jsonString, "    {\n        \"level\": %d,\n        \"pid\": %d,\n        \"ppid\": %d\n    },\n", level, getpid(), getppid());
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

	getProcessData("processesArray", 0, stream);
	fflush(stream);

	getProcessData("processData", level, stream);
	fflush(stream);

        for (int i = 1; i <= numberOfCalls; i++) {
		if (fork() == 0) {
			if (i == numberOfCalls) {
				getProcessData("lastProcess", ++level, stream);
			}
			else { 
				getProcessData("processData", ++level, stream);
			}
		} else {
			wait(0);
			break;
		}	
        	fflush(stream);
	}
	fclose(stream);
        return 0;
}
