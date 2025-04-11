package stanism.marketplace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stanism.marketplace.model.Image;
import stanism.marketplace.model.Item;
import stanism.marketplace.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ImageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Transactional
    public Image saveImage(Image image) {
        logger.info("Saving image with URL: {}", image.getImageUrl());
        Image savedImage = imageRepository.save(image);
        logger.info("Successfully saved image with ID: {}", savedImage.getId());
        return savedImage;
    }

    @Transactional
    public void deleteImage(Long id) {
        logger.info("Deleting image with ID: {}", id);
        imageRepository.deleteById(id);
        logger.info("Successfully deleted image with ID: {}", id);
    }
}