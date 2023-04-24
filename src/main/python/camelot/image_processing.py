# -*- coding: utf-8 -*-

import cv2
import numpy as np

def adaptive_threshold_with_img(img, process_background=False, blocksize=15, c=-2):
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    if process_background:
        threshold = cv2.adaptiveThreshold(
            gray, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY, blocksize, c
        )
    else:
        threshold = cv2.adaptiveThreshold(
            np.invert(gray),
            255,
            cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
            cv2.THRESH_BINARY,
            blocksize,
            c,
        )
    return img, threshold

def adaptive_threshold(imagename, process_background=False, blocksize=15, c=-2):
    """Thresholds an image using OpenCV's adaptiveThreshold.

    Parameters
    ----------
    imagename : string
        Path to image file.
    process_background : bool, optional (default: False)
        Whether or not to process lines that are in background.
    blocksize : int, optional (default: 15)
        Size of a pixel neighborhood that is used to calculate a
        threshold value for the pixel: 3, 5, 7, and so on.

        For more information, refer `OpenCV's adaptiveThreshold <https://docs.opencv.org/2.4/modules/imgproc/doc/miscellaneous_transformations.html#adaptivethreshold>`_.
    c : int, optional (default: -2)
        Constant subtracted from the mean or weighted mean.
        Normally, it is positive but may be zero or negative as well.

        For more information, refer `OpenCV's adaptiveThreshold <https://docs.opencv.org/2.4/modules/imgproc/doc/miscellaneous_transformations.html#adaptivethreshold>`_.

    Returns
    -------
    img : object
        numpy.ndarray representing the original image.
    threshold : object
        numpy.ndarray representing the thresholded image.

    """
    img = cv2.imread(imagename)
    img, threshold = adaptive_threshold_with_img(img, process_background, blocksize, c)
    return img, threshold


def find_lines(
    threshold, regions=None, direction="horizontal", line_scale=15, iterations=0
):
    """Finds horizontal and vertical lines by applying morphological
    transformations on an image.

    Parameters
    ----------
    threshold : object
        numpy.ndarray representing the thresholded image.
    regions : list, optional (default: None)
        List of page regions that may contain tables of the form x1,y1,x2,y2
        where (x1, y1) -> left-top and (x2, y2) -> right-bottom
        in image coordinate space.
    direction : string, optional (default: 'horizontal')
        Specifies whether to find vertical or horizontal lines.
    line_scale : int, optional (default: 15)
        Factor by which the page dimensions will be divided to get
        smallest length of lines that should be detected.

        The larger this value, smaller the detected lines. Making it
        too large will lead to text being detected as lines.
    iterations : int, optional (default: 0)
        Number of times for erosion/dilation is applied.

        For more information, refer `OpenCV's dilate <https://docs.opencv.org/2.4/modules/imgproc/doc/filtering.html#dilate>`_.

    Returns
    -------
    dmask : object
        numpy.ndarray representing pixels where vertical/horizontal
        lines lie.
    lines : list
        List of tuples representing vertical/horizontal lines with
        coordinates relative to a left-top origin in
        image coordinate space.

    """
    lines = []

    if direction == "vertical":
        size = threshold.shape[0] // line_scale
        el = cv2.getStructuringElement(cv2.MORPH_RECT, (1, size))
    elif direction == "horizontal":
        size = threshold.shape[1] // line_scale
        el = cv2.getStructuringElement(cv2.MORPH_RECT, (size, 1))
    elif direction is None:
        raise ValueError("Specify direction as either 'vertical' or 'horizontal'")

    if regions is not None:
        region_mask = np.zeros(threshold.shape)
        for region in regions:
            x, y, w, h = region
            region_mask[y : y + h, x : x + w] = 1
        threshold = np.multiply(threshold, region_mask)

    threshold = cv2.erode(threshold, el)
    threshold = cv2.dilate(threshold, el)
    dmask = cv2.dilate(threshold, el, iterations=iterations)

    try:
        _, contours, _ = cv2.findContours(
            threshold.astype(np.uint8), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE
        )
    except ValueError:
        # for opencv backward compatibility
        contours, _ = cv2.findContours(
            threshold.astype(np.uint8), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE
        )

    for c in contours:
        x, y, w, h = cv2.boundingRect(c)
        x1, x2 = x, x + w
        y1, y2 = y, y + h
        if direction == "vertical":
            lines.append(((x1 + x2) // 2, y2, (x1 + x2) // 2, y1))
        elif direction == "horizontal":
            lines.append((x1, (y1 + y2) // 2, x2, (y1 + y2) // 2))

    return dmask, lines


def find_contours(vertical, horizontal):
    """Finds table boundaries using OpenCV's findContours.

    Parameters
    ----------
    vertical : object
        numpy.ndarray representing pixels where vertical lines lie.
    horizontal : object
        numpy.ndarray representing pixels where horizontal lines lie.

    Returns
    -------
    cont : list
        List of tuples representing table boundaries. Each tuple is of
        the form (x, y, w, h) where (x, y) -> left-top, w -> width and
        h -> height in image coordinate space.

    """
    mask = vertical + horizontal

    try:
        __, contours, __ = cv2.findContours(
            mask.astype(np.uint8), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE
        )
    except ValueError:
        # for opencv backward compatibility
        contours, __ = cv2.findContours(
            mask.astype(np.uint8), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE
        )
    # sort in reverse based on contour area and use first 10 contours
    contours = sorted(contours, key=cv2.contourArea, reverse=True)[:10]

    cont = []
    for c in contours:
        c_poly = cv2.approxPolyDP(c, 3, True)
        x, y, w, h = cv2.boundingRect(c_poly)
        cont.append((x, y, w, h))
    return cont


def find_joints(contours, vertical, horizontal):
    """Finds joints/intersections present inside each table boundary.

    Parameters
    ----------
    contours : list
        List of tuples representing table boundaries. Each tuple is of
        the form (x, y, w, h) where (x, y) -> left-top, w -> width and
        h -> height in image coordinate space.
    vertical : object
        numpy.ndarray representing pixels where vertical lines lie.
    horizontal : object
        numpy.ndarray representing pixels where horizontal lines lie.

    Returns
    -------
    tables : dict
        Dict with table boundaries as keys and list of intersections
        in that boundary as their value.
        Keys are of the form (x1, y1, x2, y2) where (x1, y1) -> lb
        and (x2, y2) -> rt in image coordinate space.

    """
    joints = np.multiply(vertical, horizontal)
    tables = {}
    for c in contours:
        x, y, w, h = c
        roi = joints[y : y + h, x : x + w]
        try:
            __, jc, __ = cv2.findContours(
                roi.astype(np.uint8), cv2.RETR_CCOMP, cv2.CHAIN_APPROX_SIMPLE
            )
        except ValueError:
            # for opencv backward compatibility
            jc, __ = cv2.findContours(
                roi.astype(np.uint8), cv2.RETR_CCOMP, cv2.CHAIN_APPROX_SIMPLE
            )
        if len(jc) <= 4:  # remove contours with less than 4 joints
            continue
        joint_coords = []
        for j in jc:
            jx, jy, jw, jh = cv2.boundingRect(j)
            c1, c2 = x + (2 * jx + jw) // 2, y + (2 * jy + jh) // 2
            joint_coords.append((c1, c2))
        tables[(x, y + h, x + w, y)] = joint_coords

    return tables


def intersectes(r1, r2):
    """ Checking the intersection of two ribs.

    :param r1: tuple
        (x11, y11, x21, y21) where (x11, y11) -> start coordinates of r1
        and (x21, y21) -> end coordinates of rib1.
    :param r2: tuple
        (x12, y12, x22, y22) where (x12, y12) -> start coordinates of r2
        and (x22, y22) -> end coordinates of rib2.
    :return: boolean
        if ribs intersect True else False.
    """
    c_m = 10
    x11, y11, x21, y21 = r1[0], r1[1], r1[2], r1[3]
    x12, y12, x22, y22 = r2[0], r2[1], r2[2], r2[3]

    if (x11 == x21 and x12 == x22) or (y11 == y21 and y12 == y22):
        return False
    elif x11 == x21 and y12 == y22:
        return x11 + c_m >= x12 and x11 <= x22 + c_m \
            and y12 <= y11 + c_m and y12 >= y21 - c_m
    else:
        return x12 + c_m >= x11 and x12 <= x21 + c_m \
            and y11 <= y12 + c_m and y11 >= y22 - c_m


def draw_v(image, h_lines):
    """
    Draws the vertical lines between given horisontal lines, corrects the image.

    :param image: img : object
        numpy.ndarray representing the image.
    :param h_lines: list
        List of tuples representing horizontal lines with coordinates.
    :return: img : object
        numpy.ndarray representing the new image.
    """

    if len(h_lines) > 0:

        h_lines = sorted(h_lines, key=lambda x: (x[0], x[1]))

        l_x, r_x = h_lines[0][0], h_lines[0][2]
        u_y, d_y = h_lines[0][1], h_lines[0][1]

        for i in range(len(h_lines)):

            if l_x == h_lines[i][0] and i != len(h_lines) - 1:
                r_x = max(r_x, h_lines[i][2])

            elif l_x == h_lines[i][0]:
                d_y = h_lines[i][3]
                cv2.rectangle(image, pt1=(l_x, u_y), pt2=(r_x, d_y), color=(0, 0, 0), thickness=3)

            else:
                d_y = h_lines[i - 1][3]
                cv2.rectangle(image, pt1=(l_x, u_y), pt2=(r_x, d_y), color=(0, 0, 0), thickness=3)
                l_x, r_x = h_lines[i][0], h_lines[i][2]
                u_y, d_y = h_lines[i][1], h_lines[i][3]


    return image


def draw_h(image, v_lines):
    '''
        Draws the horisontal lines between given vertical lines, corrects the image.

        :param image: img : object
            numpy.ndarray representing the image.
        :param v_lines: list
            List of tuples representing vertical lines with
            coordinates.
        :return: image : object
            numpy.ndarray representing the new image.
    '''
    if (len(v_lines) > 0):
        v_lines = sorted(v_lines, key=lambda x: (x[3], x[0]))

        u_y, d_y = v_lines[0][3], v_lines[0][1]

        for i in range(len(v_lines)):

            if u_y == v_lines[i][3] and i != len(v_lines) - 1:
                d_y = max(d_y, v_lines[i][1])

            elif u_y == v_lines[i][3]:
                d_y = max(d_y, v_lines[i][1])
                cv2.rectangle(image, pt1=(50, u_y), pt2=(image.shape[1] - 50, d_y), color=(0, 0, 0), thickness=3)

            else:
                cv2.rectangle(image, pt1=(50, u_y), pt2=(image.shape[1] - 50, d_y), color=(0, 0, 0), thickness=3)
                u_y, d_y = v_lines[i][3], v_lines[i][1]

    return image

def correct_lines(image, v_segments, h_segments):
    '''

    :param image: object
            numpy.ndarray representing the image.
    :param v_segments: list
            List of tuples representing vertical lines with
            coordinates.
    :param h_segments: list
        List of tuples representing horizontal lines with
        coordinates.
    :return: image : object
            numpy.ndarray representing the new image.
    '''

    h_size, v_size = len(h_segments), len(v_segments)

    if h_size > 1 and v_size == 0:
        image = draw_v(image, h_segments)

    elif h_size == 0 and v_size > 1:
        image = draw_h(image, v_segments)

    elif v_size >= 1 and h_size >= 1:

        ribs = v_segments[:] + h_segments[:]
        segments = [[ribs[i]][:] for i in range(len(ribs))]

        for i in range(0, len(ribs) - 1):
            for j in range(i+1, len(ribs)):
                if intersectes(ribs[i],ribs[j]):
                    for sg1 in segments:
                        cur_sg = []
                        if ribs[i] in sg1:
                            cur_sg = sg1
                            break

                    for sg2 in segments:
                        del_sg = []
                        if ribs[j] in sg2 and cur_sg != sg2:
                            cur_sg += sg2[:]
                            del_sg = sg2
                            break
                    if del_sg in segments:
                        segments.remove(del_sg)


        s_lines = []

        for i in range(len(segments)):

            min_x, min_y = segments[i][0][0], segments[i][0][3]
            max_x, max_y = segments[i][0][2], segments[i][0][1]

            if len(segments[i]) > 1:
                for line in segments[i]:
                    min_x, min_y = min(min_x, line[0]),min(min_y, line[3])
                    max_x, max_y = max(max_x, line[2]), max(max_y,line[1])
                    cv2.rectangle(image, pt1=(min_x, min_y), pt2=(max_x, max_y), color=(0, 0, 0), thickness=3)
            else:
                s_lines += segments[i]

        h_s_lines, v_s_lines = [], []

        for line in s_lines:
            v_s_lines.append(line) if line[0] == line[2] else h_s_lines.append(line)

        image = draw_h(image, v_s_lines)
        image = draw_v(image, h_s_lines)

    '''cv2.imshow("Image", image)
    cv2.waitKey(0)
    cv2.destroyAllWindows()
    '''
    return image

