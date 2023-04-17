package compress.printer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import compress.model.HuffNode;

public class BinaryTreePrinter {
    
    // Helper method to print the binary tree
    public static void printBinaryTree(HuffNode root) {
        int maxLevel = getMaxLevel(root);

        printNodeInternal(Collections.singletonList(root), 1, maxLevel);
    }

    // Helper method to get the maximum depth of the binary tree
    private static int getMaxLevel(HuffNode node) {
        if (node.isLeaf()) {
            return 0;
        }

        return Math.max(getMaxLevel(node.left()), getMaxLevel(node.right())) + 1;
    }

    // Helper method to print the nodes of the binary tree
    private static void printNodeInternal(List<HuffNode> nodes, int level, int maxLevel) {
        if (nodes.isEmpty() || isAllElementsNull(nodes)) {
            return;
        }

        int floor = maxLevel - level;
        int edgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
        int firstSpaces = (int) Math.pow(2, (floor)) - 1;
        int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

        printWhitespaces(firstSpaces);

        List<HuffNode> newNodes = new ArrayList<>();
        for (HuffNode node : nodes) {
            if (node != null) {
                System.out.print(node.valueString().length() > 1 ? "o" : node.valueString());
                newNodes.add(node.left());
                newNodes.add(node.right());
            } else {
                newNodes.add(null);
                newNodes.add(null);
                System.out.print("  ");
            }

            printWhitespaces(betweenSpaces);
        }
        System.out.println("");

        for (int i = 1; i <= edgeLines; i++) {
            for (int j = 0; j < nodes.size(); j++) {
                printWhitespaces(firstSpaces - i);
                if (nodes.get(j) == null) {
                    printWhitespaces(edgeLines + edgeLines + i + 1);
                    continue;
                }

                if (nodes.get(j).left() != null) {
                    System.out.print("/");
                } else {
                    printWhitespaces(1);
                }

                printWhitespaces(i + i - 1);

                if (nodes.get(j).right() != null) {
                    System.out.print("\\");
                } else {
                    printWhitespaces(1);
                }

                printWhitespaces(edgeLines + edgeLines - i);
            }

            System.out.println("");
        }

        printNodeInternal(newNodes, level + 1, maxLevel);
    }

    // Helper method to check if all elements of a list are null
    private static boolean isAllElementsNull(List<?> list) {
        for (Object object : list) {
            if (object != null) {
                return false;
            }
        }

        return true;
    }

    // Helper method to print white spaces
    private static void printWhitespaces(int count) {
        for (int i = 0; i < count; i++) {
            System.out.print(" ");
        }
    }
}
