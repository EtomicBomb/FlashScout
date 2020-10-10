package point_filled_checker;

import linear_algebra.Point;

import java.util.*;

class AlignerCentersFinder {
    static Point[] findAlignerCenters(BooleanMatrix booleanMatrix) throws Exception {
        int base = booleanMatrix.base;
        int height = booleanMatrix.height;

        HasSeen hasSeen = new HasSeen(booleanMatrix);
        Stack<IntPoint> allocatedStack = new Stack<>();

        ArrayList<Region> aligners = new ArrayList<>();

        for (int y=0; y<height; y++) {
            for (int x = 0; x < base; x++) {
                if (hasSeen.neverSeen(x, y)) {
                    Region region = floodFill(x, y, hasSeen, allocatedStack);

                    if (region.isAligner()) {
                        aligners.add(region);
                    }
                }
            }
        }


        if (aligners.size() < 4) {
            throw new Exception("Found fewer than four aligners");
        }

        aligners.sort(Comparator.comparing(Region::getPixelCount));

        ArrayList<Point> biggest = new ArrayList<>(4); // should contain the biggest four aligners
        for (var i=aligners.size()-4; i<aligners.size(); i++) {
            Region region = aligners.get(i);
            biggest.add(new Point((double)region.centroidX(), (double)region.centroidY()));
        }
        assert biggest.size() == 4;

        Point[] ret = {
            removeOnePoint(biggest, (a, b) -> Double.compare(a.x + a.y, b.x + b.y)),
            removeOnePoint(biggest, (a, b) -> Double.compare(b.x - b.y, a.x - a.y)),
            removeOnePoint(biggest, (a, b) -> Double.compare(b.x + b.y, a.x + a.y)),
            removeOnePoint(biggest, (a, b) -> Double.compare(a.x - a.y, b.x - b.y))
        };
        return ret;
    }

    private static Point removeOnePoint(ArrayList<Point> points, Comparator<Point> shouldPrefer) {
        int maxIndex = 0;
        Point maxPoint = points.get(maxIndex);

        for (int i=1; i<points.size(); i++) {
            Point point = points.get(i);
            if (shouldPrefer.compare(maxPoint, point) > 0) {
                maxIndex = i;
                maxPoint = point;
            }
        }

        return points.remove(maxIndex);
    }



    private static Region floodFill(int x, int y, HasSeen hasSeen, Stack<IntPoint> stack) {
        Region region = new Region(x, y); // FIXME: all regions will be one pixel larger than they actually are because they are initialized with a pixel, but its added again in the first iteration of the loop


        // the reason we are passing in the stack is that we want to preserve the region of memory allocated by previous calls to this function
        stack.clear(); // just make sure the length is zero
        stack.push(new IntPoint(x, y));

        while (!stack.empty()) {
            IntPoint top = stack.pop();
            int newX = top.x;
            int newY = top.y;

            if (hasSeen.hasSeen(newX, newY)) { // TODO: check perf compared to checking isBarrier before pushing onto stack
                continue;
            }
            hasSeen.markSeen(newX, newY);
            region.addPixel(newX, newY);

            if (newX > 0 && !hasSeen.isBarrier(newX-1, newY)) {
                stack.push(new IntPoint(newX-1, newY));
            }
            if (newX < hasSeen.base-1 && !hasSeen.isBarrier(newX+1, newY)) {
                stack.push(new IntPoint(newX+1, newY));
            }
            if (newY > 0 && !hasSeen.isBarrier(newX, newY-1)) {
                stack.push(new IntPoint(newX, newY-1));
            }
            if (newY < hasSeen.height-1 && !hasSeen.isBarrier(newX, newY+1)) {
                stack.push(new IntPoint(newX, newY+1));
            }
        }


        return region;
    }

    static class IntPoint {
        int x;
        int y;

        IntPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        IntPoint(Region r) {
            this.x = r.centroidX();
            this.y = r.centroidY();
        }

        public String toString() {
            return "("+x+", "+y+")";
        }
    }

    private static class HasSeen {
        SeenState[][] data;
        int base;
        int height;

        HasSeen(BooleanMatrix booleanMatrix) {
            base = booleanMatrix.base;
            height = booleanMatrix.height;

            data = new SeenState[height][base];


            for (int y=0; y<height; y++) {
                for (int x = 0; x < base; x++) {
                    SeenState color = booleanMatrix.isSet(x, y)? SeenState.NEVER_SEEN : SeenState.BARRIER;

                    data[y][x] = color;
                }
            }
        }

        boolean neverSeen(int x, int y) {
            return data[y][x] == SeenState.NEVER_SEEN;
        }

        boolean isBarrier(int x, int y) {
            return data[y][x] == SeenState.BARRIER;
        }

        void markSeen(int x, int y) {
            data[y][x] = SeenState.SEEN;
        }

        boolean hasSeen(int x, int y) {
            return data[y][x] == SeenState.SEEN;
        }
    }

    private enum SeenState {
        BARRIER,
        NEVER_SEEN,
        SEEN;
    }
}