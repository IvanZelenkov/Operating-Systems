#include <stdio.h>
#include <stdlib.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <unistd.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <string.h>

#define MAX 200
#define PERMISSIONS 0666

struct message_buffer {
	long message_type;
	char message_text[MAX];
} buffer;

void delete_queues(int message_id1, int message_id2);
void writer(int message_id);
void reader(int message_id);
void parent(int message_id, int message_id2);
void child(int message_id1, int message_id2);

int main() {
	int message_id1, message_id2;

	message_id1 = msgget(IPC_PRIVATE, PERMISSIONS | IPC_CREAT);
	message_id2 = msgget(IPC_PRIVATE, PERMISSIONS | IPC_CREAT);

	if (message_id1 == -1 || message_id2 == -1) 
		perror("msgget");

	switch (fork()) {
		case -1:
			perror("fork");
			exit(EXIT_FAILURE);
		case 0: 
			child(message_id1, message_id2);
			exit(EXIT_SUCCESS);
		default:
			parent(message_id1, message_id2);
			exit(EXIT_SUCCESS);	
	}
	delete_queues(message_id1, message_id2);
	return 0;
}

void delete_queues(int message_id1, int message_id2) {
	if (message_id1 != -1) 
        msgctl(message_id1, IPC_RMID, NULL);
    
    if (message_id2 != -1) 
        msgctl(message_id2, IPC_RMID, NULL);
}

void writer(int message_id) {
	buffer.message_type = 1;
	if (fgets(buffer.message_text, sizeof(buffer.message_text), stdin) == NULL) {
		perror("Error (fgets): can't accept input.");
		exit(EXIT_FAILURE);
	}

	// Remove trailing newline character from fgets() input.
	buffer.message_text[strcspn(buffer.message_text, "\n")] = '\0';

	if (msgsnd(message_id, &buffer, sizeof(buffer.message_text), 0) == -1) {
		perror("Error (msgsnd): message not sent.");
		exit(EXIT_FAILURE);
	}
}

void reader(int message_id) {
	if (msgrcv(message_id, &buffer, sizeof(buffer.message_text), 1, 0 & IPC_NOWAIT) == -1) {
		perror("Error (msgrcv): message not received.");
        exit(EXIT_FAILURE);
	}
}

void parent(int message_id1, int message_id2) {
	printf("I am a parent %d!\n", getpid());
	printf("I need the following chore done: ");
	writer(message_id1);
	printf("I sent the chore.\n\n");
	
	wait(0);

	printf("I am a parent %d!\n", getpid());
	reader(message_id2);
	printf("My child says: %s\n", buffer.message_text);
	delete_queues(message_id1, message_id2);
}

void child(int message_id1, int message_id2) {
	reader(message_id1);
	printf("I am a child %d!\n", getpid());
	printf("My chore is: %s\n", buffer.message_text);
	printf("Sending my parent: ");
	writer(message_id2);
	printf("I sent a reply to my parent.\n\n");
}