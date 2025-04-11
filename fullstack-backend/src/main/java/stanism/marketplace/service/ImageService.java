package stanism.marketplace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stanism.marketplace.model.Image;

import stanism.marketplace.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ImageService {
    /** Logger for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    /** Repository for handling image persistence. */
    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Transactional
    public Image saveImage(Image image) {
        LOGGER.info("Saving image with URL: {}", image.getImageUrl());
        Image savedImage = imageRepository.save(image);
        LOGGER.info("Successfully saved image with ID: {}", savedImage.getId());
        return savedImage;
    }

    @Transactional
    public void deleteImage(Long id) {
        LOGGER.info("Deleting image with ID: {}", id);
        imageRepository.deleteById(id);
        LOGGER.info("Successfully deleted image with ID: {}", id);
    }
}