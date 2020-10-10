package linear_algebra;

// NOTICE:
// I didn't come up with any of the code in this file. It is all taken from the getPerspectiveTransform and warpPerspective functions in
// https://github.com/opencv/opencv
// I didn't want to use opencv as a library dependancy because I didn't want to include their massive libary when I am only using two functions.
// This is their copyright notice:


////////////////////////////////////////////////////////////////////////////////////////
//
//  IMPORTANT: READ BEFORE DOWNLOADING, COPYING, INSTALLING OR USING.
//
//  By downloading, copying, installing or using the software you agree to this license.
//  If you do not agree to this license, do not download, install,
//  copy or use the software.
//
//
//                           License Agreement
//                For Open Source Computer Vision Library
//
// Copyright (C) 2000-2008, Intel Corporation, all rights reserved.
// Copyright (C) 2009, Willow Garage Inc., all rights reserved.
// Copyright (C) 2014-2015, Itseez Inc., all rights reserved.
// Third party copyrights are property of their respective owners.
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
//   * Redistribution's of source code must retain the above copyright notice,
//     this list of conditions and the following disclaimer.
//
//   * Redistribution's in binary form must reproduce the above copyright notice,
//     this list of conditions and the following disclaimer in the documentation
//     and/or other materials provided with the distribution.
//
//   * The name of the copyright holders may not be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// This software is provided by the copyright holders and contributors "as is" and
// any express or implied warranties, including, but not limited to, the implied
// warranties of merchantability and fitness for a particular purpose are disclaimed.
// In no event shall the Intel Corporation or contributors be liable for any direct,
// indirect, incidental, special, exemplary, or consequential damages
// (including, but not limited to, procurement of substitute goods or services;
// loss of use, data, or profits; or business interruption) however caused
// and on any theory of liability, whether in contract, strict liability,
// or tort (including negligence or otherwise) arising in any way out of
// the use of this software, even if advised of the possibility of such damage.
////////////////////////////////////////////////////////////////////////////////////////




public class Transform {
    public static Matrix getPerspectiveTransformMatrix(Point[] src, Point[] dst) throws Exception {
        // their docs https://docs.opencv.org/2.4/modules/imgproc/doc/geometric_transformations.html#Mat%20getPerspectiveTransform(InputArray%20src,%20InputArray%20dst)
        // actual source: https://github.com/opencv/opencv/blob/11b020b9f9e111bddd40bffe3b1759aa02d966f0/modules/imgproc/src/imgwarp.cpp#L3025-L3052

        Matrix a = Matrix.zeros(8, 8);
        Vector b = Vector.zeros(8);

        for (int i = 0; i < 4; i++) {
            a.set(3, i + 4, src[i].x);
            a.set(0, i, src[i].x);

            a.set(4, i + 4, src[i].y);
            a.set(1, i, src[i].y);

            a.set(5, i + 4, 1);
            a.set(2, i, 1);

            a.set(6, i, -src[i].x * dst[i].x);
            a.set(7, i, -src[i].y * dst[i].x);
            a.set(6, i + 4, -src[i].x * dst[i].y);
            a.set(7, i + 4, -src[i].y * dst[i].y);

            b.set(i, dst[i].x);
            b.set(i + 4, dst[i].y);
        }

        Vector solution = a.solve(b);
        double[] constants = solution.data;

        double[][] newMatrixData = new double[3][3];

        int index = 0;
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {
                newMatrixData[j][i] = index == 8 ? 1 : constants[index];
                index++;
            }
        }

        Matrix newMatrix = new Matrix(newMatrixData, 3, 3);
        return Matrix.invert(newMatrix);
    }

    public static Point transformPoint(Matrix m, Point p) {
        // source: https://docs.opencv.org/2.4/modules/imgproc/doc/geometric_transformations.html?highlight=getperspectivetransform#void%20warpPerspective(InputArray%20src,%20OutputArray%20dst,%20InputArray%20M,%20Size%20dsize,%20int%20flags,%20int%20borderMode,%20const%20Scalar&%20borderValue)

        double denom = m.get(0, 2)*p.x + m.get(1,2)*p.y + m.get(2,2);

        double newX = (m.get(0,0)*p.x + m.get(1,0)*p.y + m.get(2,0)) / denom;
        double newY = (m.get(0,1)*p.x + m.get(1,1)*p.y + m.get(2,1)) / denom;

        return new Point(newX, newY);
    }
}
