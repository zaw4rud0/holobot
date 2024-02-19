package dev.zawarudo.holo.modules.aoc.graph;

import dev.zawarudo.holo.modules.aoc.data.AdventDay;

import java.awt.*;

public class StackedBarChart extends BarChart {

    public StackedBarChart(int year, int leaderboardId, String sessionKey) {
        super(year, leaderboardId, sessionKey);
    }

    @Override
    protected void renderStarCountBars(Graphics2D g2d, AdventDay day, int startX, int graphHeight, int thickness) {
        int xPos = startX + thickness + thickness / 4;
        int yPos = 0;
        thickness *= 3;

        yPos = renderSingleBar(g2d, theme.getTwoStarsColor(), day.goldCount(), xPos, yPos, graphHeight, thickness);
        yPos = renderSingleBar(g2d, theme.getOneStarColor(), day.silverCount(), xPos, yPos, graphHeight, thickness);
        renderSingleBar(g2d, theme.getNoStarsColor(), day.grayCount(), xPos, yPos, graphHeight, thickness);
    }

    private int renderSingleBar(Graphics2D g2d, Paint color, int count, int x, int y, int graphHeight, int thickness) {
        g2d.setPaint(color);
        int heightBar = graphHeight * count / maxCount;
        g2d.fillRect(x, OFFSET_Y1 + (graphHeight - heightBar - y), thickness, heightBar);
        y += heightBar;
        return y;
    }
}