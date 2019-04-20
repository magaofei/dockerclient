import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.ExecCreation;

public class DockerClientDemo {


    public static void main(String[] args) throws Exception {
        final DockerClient docker = DefaultDockerClient.fromEnv().build();

        docker.pull("openjdk:8");
        final ContainerConfig containerConfig = ContainerConfig.builder()
                .image("openjdk:8")
                .cmd("sh", "-c", "java", "-version")
                .build();
        final ContainerCreation creation = docker.createContainer(containerConfig);
        final String id = creation.id();
        final ContainerInfo info = docker.inspectContainer(id);


        docker.startContainer(id);
        final String[] command = {"sh", "-c", "ls"};
        final ExecCreation execCreation2 = docker.execCreate(
                id, command, DockerClient.ExecCreateParam.attachStdout(),DockerClient.ExecCreateParam.attachStderr()
        );

        final ExecCreation execCreation = docker.execCreate(
                id, command, DockerClient.ExecCreateParam.attachStdout(),
                DockerClient.ExecCreateParam.attachStderr());
        final LogStream output = docker.execStart(execCreation.id());
        final String execOutput = output.readFully();

        System.out.println("execOutput = " + execOutput);

// Kill container
        docker.killContainer(id);

// Remove container
        docker.removeContainer(id);

// Close the docker client
        docker.close();

    }
}
