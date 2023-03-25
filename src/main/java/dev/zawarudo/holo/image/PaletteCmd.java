package dev.zawarudo.holo.image;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.annotations.Deactivated;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

@Deactivated
@Command(name = "palette",
        description = "",
        category = CommandCategory.IMAGE)
public class PaletteCmd extends AbstractCommand {

    public static void main(String[] args) {
        PaletteCmd cmd = new PaletteCmd();
        cmd.doStuff();
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        event.getChannel().sendMessage("This feature is not implemented yet!").queue();
    }

    /**
     *         <dependency>
     *             <groupId>org.bytedeco</groupId>
     *             <artifactId>javacv-platform</artifactId>
     *             <version>1.5.8</version>
     *         </dependency>
     */

    private void doStuff() {
        /*
        BufferedImage inputImage;
        try {
            inputImage = ImageIO.read(new URL("https://media.discordapp.net/attachments/873558142624075826/1086744813543620638/25.png"));
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Something went wrong while reading the image.", ex);
            }
            return;
        }

        // Convert BufferedImage to Mat
        Mat matInputImage = new Mat(inputImage.getHeight(), inputImage.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) inputImage.getRaster().getDataBuffer()).getData();
        matInputImage.put(0, 0, data);

        Mat grayImage = new Mat();
        Imgproc.cvtColor(matInputImage, grayImage, Imgproc.COLOR_BGR2GRAY);

        Mat blurredImage = new Mat();
        Imgproc.GaussianBlur(grayImage, blurredImage, new Size(5, 5), 0);

        Mat binaryImage = new Mat();
        Imgproc.threshold(blurredImage, binaryImage, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

        Mat contourImage = matInputImage.clone();
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (Mat mat : contours) {
            Rect boundingRect = Imgproc.boundingRect(mat);
            //Mat segment = matInputImage.submat(boundingRect);

            //Scalar averagePixel = Core.mean(segment);
            //System.out.println("Average pixel value of segment " + i + ": " + averagePixel);

            Imgproc.rectangle(contourImage, boundingRect, new Scalar(0, 255, 0), 2);
        }

        // Save output image with bounding boxes
        Imgcodecs.imwrite("output_image.jpg", contourImage);*/
    }
}