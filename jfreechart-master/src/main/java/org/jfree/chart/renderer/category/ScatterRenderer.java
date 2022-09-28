/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2022, by David Gilbert and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * --------------------
 * ScatterRenderer.java
 * --------------------
 * (C) Copyright 2007-2022, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   David Forslund;
 *                   Peter Kolb (patches 2497611, 2791407);
 *
 */

package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jfree.chart.legend.LegendItem;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.api.PublicCloneable;
import org.jfree.chart.internal.ShapeUtils;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.MultiValueCategoryDataset;

/**
 * A renderer that handles the multiple values from a
 * {@link MultiValueCategoryDataset} by plotting a shape for each value for
 * each given item in the dataset. The example shown here is generated by
 * the {@code ScatterRendererDemo1.java} program included in the
 * JFreeChart Demo Collection:
 * <br><br>
 * <img src="doc-files/ScatterRendererSample.png" alt="ScatterRendererSample.png">
 */
public class ScatterRenderer extends AbstractCategoryItemRenderer
        implements Cloneable, PublicCloneable, Serializable {

    /**
     * A table of flags that control (per series) whether or not shapes are
     * filled.
     */
    private Map<Integer, Boolean> seriesShapesFilledMap;

    /**
     * The default value returned by the getShapeFilled() method.
     */
    private boolean baseShapesFilled;

    /**
     * A flag that controls whether the fill paint is used for filling
     * shapes.
     */
    private boolean useFillPaint;

    /**
     * A flag that controls whether outlines are drawn for shapes.
     */
    private boolean drawOutlines;

    /**
     * A flag that controls whether the outline paint is used for drawing shape
     * outlines - if not, the regular series paint is used.
     */
    private boolean useOutlinePaint;

    /**
     * A flag that controls whether or not the x-position for each item is
     * offset within the category according to the series.
     */
    private boolean useSeriesOffset;

    /**
     * The item margin used for series offsetting - this allows the positioning
     * to match the bar positions of the {@link BarRenderer} class.
     */
    private double itemMargin;

    /**
     * Constructs a new renderer.
     */
    public ScatterRenderer() {
        this.seriesShapesFilledMap = new HashMap<>();
        this.baseShapesFilled = true;
        this.useFillPaint = false;
        this.drawOutlines = false;
        this.useOutlinePaint = false;
        this.useSeriesOffset = true;
        this.itemMargin = 0.20;
    }

    /**
     * Returns the flag that controls whether or not the x-position for each
     * data item is offset within the category according to the series.
     *
     * @return A boolean.
     *
     * @see #setUseSeriesOffset(boolean)
     */
    public boolean getUseSeriesOffset() {
        return this.useSeriesOffset;
    }

    /**
     * Sets the flag that controls whether or not the x-position for each
     * data item is offset within its category according to the series, and
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param offset  the offset.
     *
     * @see #getUseSeriesOffset()
     */
    public void setUseSeriesOffset(boolean offset) {
        this.useSeriesOffset = offset;
        fireChangeEvent();
    }

    /**
     * Returns the item margin, which is the gap between items within a
     * category (expressed as a percentage of the overall category width).
     * This can be used to match the offset alignment with the bars drawn by
     * a {@link BarRenderer}).
     *
     * @return The item margin.
     *
     * @see #setItemMargin(double)
     * @see #getUseSeriesOffset()
     */
    public double getItemMargin() {
        return this.itemMargin;
    }

    /**
     * Sets the item margin, which is the gap between items within a category
     * (expressed as a percentage of the overall category width), and sends
     * a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param margin  the margin (0.0 &lt;= margin &lt; 1.0).
     *
     * @see #getItemMargin()
     * @see #getUseSeriesOffset()
     */
    public void setItemMargin(double margin) {
        if (margin < 0.0 || margin >= 1.0) {
            throw new IllegalArgumentException("Requires 0.0 <= margin < 1.0.");
        }
        this.itemMargin = margin;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if outlines should be drawn for shapes, and
     * {@code false} otherwise.
     *
     * @return A boolean.
     *
     * @see #setDrawOutlines(boolean)
     */
    public boolean getDrawOutlines() {
        return this.drawOutlines;
    }

    /**
     * Sets the flag that controls whether outlines are drawn for
     * shapes, and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     * <p>In some cases, shapes look better if they do NOT have an outline, but
     * this flag allows you to set your own preference.</p>
     *
     * @param flag the flag.
     *
     * @see #getDrawOutlines()
     */
    public void setDrawOutlines(boolean flag) {
        this.drawOutlines = flag;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether the outline paint is used for
     * shape outlines.  If not, the regular series paint is used.
     *
     * @return A boolean.
     *
     * @see #setUseOutlinePaint(boolean)
     */
    public boolean getUseOutlinePaint() {
        return this.useOutlinePaint;
    }

    /**
     * Sets the flag that controls whether the outline paint is used for shape
     * outlines, and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param use the flag.
     *
     * @see #getUseOutlinePaint()
     */
    public void setUseOutlinePaint(boolean use) {
        this.useOutlinePaint = use;
        fireChangeEvent();
    }

    // SHAPES FILLED

    /**
     * Returns the flag used to control whether or not the shape for an item
     * is filled. The default implementation passes control to the
     * {@code getSeriesShapesFilled} method. You can override this method
     * if you require different behaviour.
     *
     * @param series the series index (zero-based).
     * @param item   the item index (zero-based).
     * @return A boolean.
     */
    public boolean getItemShapeFilled(int series, int item) {
        return getSeriesShapesFilled(series);
    }

    /**
     * Returns the flag used to control whether or not the shapes for a series
     * are filled.
     *
     * @param series the series index (zero-based).
     * @return A boolean.
     */
    public boolean getSeriesShapesFilled(int series) {
        Boolean flag = this.seriesShapesFilledMap.get(series);
        if (flag != null) {
            return flag;
        }
        else {
            return this.baseShapesFilled;
        }

    }

    /**
     * Sets the 'shapes filled' flag for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series the series index (zero-based).
     * @param filled the flag.
     */
    public void setSeriesShapesFilled(int series, Boolean filled) {
        this.seriesShapesFilledMap.put(series, filled);
        fireChangeEvent();
    }

    /**
     * Sets the 'shapes filled' flag for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series the series index (zero-based).
     * @param filled the flag.
     */
    public void setSeriesShapesFilled(int series, boolean filled) {
        this.seriesShapesFilledMap.put(series, filled);
        fireChangeEvent();
    }

    /**
     * Returns the base 'shape filled' attribute.
     *
     * @return The base flag.
     */
    public boolean getBaseShapesFilled() {
        return this.baseShapesFilled;
    }

    /**
     * Sets the base 'shapes filled' flag and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param flag the flag.
     */
    public void setBaseShapesFilled(boolean flag) {
        this.baseShapesFilled = flag;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if the renderer should use the fill paint
     * setting to fill shapes, and {@code false} if it should just
     * use the regular paint.
     *
     * @return A boolean.
     */
    public boolean getUseFillPaint() {
        return this.useFillPaint;
    }

    /**
     * Sets the flag that controls whether the fill paint is used to fill
     * shapes, and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param flag the flag.
     */
    public void setUseFillPaint(boolean flag) {
        this.useFillPaint = flag;
        fireChangeEvent();
    }

    /**
     * Returns the range of values the renderer requires to display all the
     * items from the specified dataset. This takes into account the range
     * between the min/max values, possibly ignoring invisible series.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @return The range (or {@code null} if the dataset is
     *         {@code null} or empty).
     */
    @Override
    public Range findRangeBounds(CategoryDataset dataset) {
         return findRangeBounds(dataset, true);
    }

    /**
     * Draw a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area in which the data is drawn.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param pass  the pass index.
     */
    @Override
    public void drawItem(Graphics2D g2, CategoryItemRendererState state,
            Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis,
            ValueAxis rangeAxis, CategoryDataset dataset, int row, int column,
            int pass) {

        // do nothing if item is not visible
        if (!getItemVisible(row, column)) {
            return;
        }
        int visibleRow = state.getVisibleSeriesIndex(row);
        if (visibleRow < 0) {
            return;
        }
        int visibleRowCount = state.getVisibleSeriesCount();

        PlotOrientation orientation = plot.getOrientation();

        MultiValueCategoryDataset d = (MultiValueCategoryDataset) dataset;
        List values = d.getValues(row, column);
        if (values == null) {
            return;
        }
        int valueCount = values.size();
        for (int i = 0; i < valueCount; i++) {
            // current data point...
            double x1;
            if (this.useSeriesOffset) {
                x1 = domainAxis.getCategorySeriesMiddle(column, 
                        dataset.getColumnCount(), visibleRow, visibleRowCount,
                        this.itemMargin, dataArea, plot.getDomainAxisEdge());
            }
            else {
                x1 = domainAxis.getCategoryMiddle(column, getColumnCount(),
                        dataArea, plot.getDomainAxisEdge());
            }
            Number n = (Number) values.get(i);
            double value = n.doubleValue();
            double y1 = rangeAxis.valueToJava2D(value, dataArea,
                    plot.getRangeAxisEdge());

            Shape shape = getItemShape(row, column);
            if (orientation == PlotOrientation.HORIZONTAL) {
                shape = ShapeUtils.createTranslatedShape(shape, y1, x1);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                shape = ShapeUtils.createTranslatedShape(shape, x1, y1);
            }
            if (getItemShapeFilled(row, column)) {
                if (this.useFillPaint) {
                    g2.setPaint(getItemFillPaint(row, column));
                }
                else {
                    g2.setPaint(getItemPaint(row, column));
                }
                g2.fill(shape);
            }
            if (this.drawOutlines) {
                if (this.useOutlinePaint) {
                    g2.setPaint(getItemOutlinePaint(row, column));
                }
                else {
                    g2.setPaint(getItemPaint(row, column));
                }
                g2.setStroke(getItemOutlineStroke(row, column));
                g2.draw(shape);
            }
        }

    }

    /**
     * Returns a legend item for a series.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return The legend item.
     */
    @Override
    public LegendItem getLegendItem(int datasetIndex, int series) {

        CategoryPlot cp = getPlot();
        if (cp == null) {
            return null;
        }

        if (isSeriesVisible(series) && isSeriesVisibleInLegend(series)) {
            CategoryDataset dataset = cp.getDataset(datasetIndex);
            String label = getLegendItemLabelGenerator().generateLabel(
                    dataset, series);
            String description = label;
            String toolTipText = null;
            if (getLegendItemToolTipGenerator() != null) {
                toolTipText = getLegendItemToolTipGenerator().generateLabel(
                        dataset, series);
            }
            String urlText = null;
            if (getLegendItemURLGenerator() != null) {
                urlText = getLegendItemURLGenerator().generateLabel(
                        dataset, series);
            }
            Shape shape = lookupLegendShape(series);
            Paint paint = lookupSeriesPaint(series);
            Paint fillPaint = (this.useFillPaint
                    ? getItemFillPaint(series, 0) : paint);
            boolean shapeOutlineVisible = this.drawOutlines;
            Paint outlinePaint = (this.useOutlinePaint
                    ? getItemOutlinePaint(series, 0) : paint);
            Stroke outlineStroke = lookupSeriesOutlineStroke(series);
            LegendItem result = new LegendItem(label, description, toolTipText,
                    urlText, true, shape, getItemShapeFilled(series, 0),
                    fillPaint, shapeOutlineVisible, outlinePaint, outlineStroke,
                    false, new Line2D.Double(-7.0, 0.0, 7.0, 0.0),
                    getItemStroke(series, 0), getItemPaint(series, 0));
            result.setLabelFont(lookupLegendTextFont(series));
            Paint labelPaint = lookupLegendTextPaint(series);
            if (labelPaint != null) {
                result.setLabelPaint(labelPaint);
            }
            result.setDataset(dataset);
            result.setDatasetIndex(datasetIndex);
            result.setSeriesKey(dataset.getRowKey(series));
            result.setSeriesIndex(series);
            return result;
        }
        return null;

    }

    /**
     * Tests this renderer for equality with an arbitrary object.
     *
     * @param obj the object ({@code null} permitted).
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ScatterRenderer)) {
            return false;
        }
        ScatterRenderer that = (ScatterRenderer) obj;
        if (!Objects.equals(this.seriesShapesFilledMap, that.seriesShapesFilledMap)) {
            return false;
        }
        if (this.baseShapesFilled != that.baseShapesFilled) {
            return false;
        }
        if (this.useFillPaint != that.useFillPaint) {
            return false;
        }
        if (this.drawOutlines != that.drawOutlines) {
            return false;
        }
        if (this.useOutlinePaint != that.useOutlinePaint) {
            return false;
        }
        if (this.useSeriesOffset != that.useSeriesOffset) {
            return false;
        }
        if (this.itemMargin != that.itemMargin) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns an independent copy of the renderer.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  should not happen.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        ScatterRenderer clone = (ScatterRenderer) super.clone();
        clone.seriesShapesFilledMap = new HashMap<>(this.seriesShapesFilledMap);
        return clone;
    }

    /**
     * Provides serialization support.
     *
     * @param stream the output stream.
     * @throws java.io.IOException if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();

    }

    /**
     * Provides serialization support.
     *
     * @param stream the input stream.
     * @throws java.io.IOException    if there is an I/O error.
     * @throws ClassNotFoundException if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();

    }

}
