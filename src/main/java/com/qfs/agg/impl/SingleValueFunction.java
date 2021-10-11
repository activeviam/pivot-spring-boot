/*
 * (C) ActiveViam 2007-2021
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.qfs.agg.impl;

import com.activeviam.fwk.ActiveViamRuntimeException;
import com.qfs.agg.IAggregation;
import com.qfs.agg.IAggregationBinding;
import com.qfs.agg.IAggregationFunction;
import com.qfs.chunk.IAllocationSettings;
import com.qfs.chunk.IArrayReader;
import com.qfs.chunk.IArrayWriter;
import com.qfs.chunk.IWritableArray;
import com.qfs.store.ChunkFactories;
import com.qfs.store.IChunkFactory;
import com.qfs.store.Types;
import com.qfs.vector.IVector;
import java.util.Arrays;
import java.util.Objects;

/**
 * Aggregation function that holds a single value.
 *
 * <p>This aggregation function does not support disaggregation so
 * it will not work with aggregate providers that maintain aggregates
 * such as the bitmap aggregate provider, when those aggregates
 * are removed or updated.
 *
 * <p>It will always work with JustInTime aggregate providers
 * that compute aggregate from scratch at each query.
 *
 * @author ActiveViam
 */
public class SingleValueFunction extends AAggregationFunction {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** Plugin value key */
	public static final String PLUGIN_KEY = IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY;

	/** Error message. */
	protected static final String AGGREGATE_MESSAGE = "The values to aggregate are expected to be equal (%s, %s)";

	/**
	 * Constructor.
	 */
	public SingleValueFunction() {
		super(PLUGIN_KEY);
	}

	@Override
	public Object key() {
		return PLUGIN_KEY;
	}

	@Override
	public IChunkFactory<?> createAggregateChunkFactory(int inputDataType, boolean isTransient,
			IAllocationSettings allocationSettings) {
		if (Types.isDictionary(inputDataType)) {
			throw unsupportedInputDataType(inputDataType);
		} else if (Types.isPrimitive(inputDataType)) {
			final boolean nullable = Types.isNullable(inputDataType);
			switch (Types.getContentType(inputDataType)) {
				case Types.CONTENT_DOUBLE:
					return ChunkFactories.doubleChunkFactory(isTransient, allocationSettings, nullable);
				case Types.CONTENT_FLOAT:
					return ChunkFactories.floatChunkFactory(isTransient, allocationSettings, nullable);
				case Types.CONTENT_LONG:
					return ChunkFactories.longChunkFactory(isTransient, allocationSettings, nullable);
				case Types.CONTENT_INT:
					return ChunkFactories.intChunkFactory(isTransient, allocationSettings, nullable);
				case Types.CONTENT_BOOLEAN:
					return nullable
							? ChunkFactories.objectChunkFactory(isTransient, allocationSettings)
							: ChunkFactories.booleanChunkFactory(isTransient, allocationSettings);
				default:
					throw unsupportedInputDataType(inputDataType);
			}
		} else if (Types.isArray(inputDataType)) {
			// Arrays are handled as plain Objects
			return ChunkFactories.objectChunkFactory(isTransient, allocationSettings);
		} else {
			// When not specified we handle plain Objects
			return ChunkFactories.objectChunkFactory(isTransient, allocationSettings);
		}
	}

	@Override
	public IAggregation createAggregation(final String fieldName, final int inputDataType) {
		if (Types.isDictionary(inputDataType)) {
			throw unsupportedInputDataType(inputDataType);
		} else if (Types.isPrimitive(inputDataType)) {
			if (Types.isNullable(inputDataType)) {
				switch (Types.getContentType(inputDataType)) {
					case Types.CONTENT_DOUBLE:
						return new SingleValueAggregationDoubleNullable(fieldName, this, inputDataType);
					case Types.CONTENT_FLOAT:
						return new SingleValueAggregationFloatNullable(fieldName, this, inputDataType);
					case Types.CONTENT_LONG:
						return new SingleValueAggregationLongNullable(fieldName, this, inputDataType);
					case Types.CONTENT_INT:
						return new SingleValueAggregationIntNullable(fieldName, this, inputDataType);
					case Types.CONTENT_BOOLEAN:
						return new SingleValueAggregationObject(fieldName, this, inputDataType);
					default: throw unsupportedInputDataType(inputDataType);
				}
			} else {
				switch (Types.getContentType(inputDataType)) {
					case Types.CONTENT_DOUBLE:
						return new SingleValueAggregationDouble(fieldName, this, inputDataType);
					case Types.CONTENT_FLOAT:
						return new SingleValueAggregationFloat(fieldName, this, inputDataType);
					case Types.CONTENT_LONG:
						return new SingleValueAggregationLong(fieldName, this, inputDataType);
					case Types.CONTENT_INT:
						return new SingleValueAggregationInt(fieldName, this, inputDataType);
					case Types.CONTENT_BOOLEAN:
						return new SingleValueAggregationBoolean(fieldName, this, inputDataType);
					default: throw unsupportedInputDataType(inputDataType);
				}
			}
		} else if (Types.isArray(inputDataType)) {
			// Arrays are handled as plain Objects
			return new SingleValueAggregationArrayObject(fieldName, this, inputDataType);
		} else {
			// When not specified we expect plain Objects
			return new SingleValueAggregationObject(fieldName, this, inputDataType);
		}
	}

	// AGGREGATIONS

	/** Single value aggregation for the 'long' data type. */
	protected static class SingleValueAggregationLong extends AAggregation<SingleValueFunction> {

		/**
		 * Constructor.
		 *
		 * @param id A String that identifies this operation
		 * @param aggFun The {@link SingleValueFunction} that created this aggregation
		 * @param dataType The data {@link Types type} of the data elements that will be aggregated
		 */
		public SingleValueAggregationLong(final String id, final SingleValueFunction aggFun, final int dataType) {
			super(id, aggFun, dataType);
		}

		@Override
		public IAggregationBinding bindSource(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingLong(source, (IWritableArray) destination);
		}

		@Override
		public IAggregationBinding bindAggregates(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingLong(source, (IWritableArray) destination);
		}

	}

	/** Single value aggregation for the 'int' data type. */
	protected static class SingleValueAggregationInt extends AAggregation<SingleValueFunction> {

		/**
		 * Constructor.
		 *
		 * @param id A String that identifies this operation
		 * @param aggFun The {@link SingleValueFunction} that created this aggregation
		 * @param dataType The data {@link Types type} of the data elements that will be aggregated
		 */
		public SingleValueAggregationInt(final String id, final SingleValueFunction aggFun, final int dataType) {
			super(id, aggFun, dataType);
		}

		@Override
		public IAggregationBinding bindSource(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingInteger(source, (IWritableArray) destination);
		}

		@Override
		public IAggregationBinding bindAggregates(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingInteger(source, (IWritableArray) destination);
		}

	}

	/** Single value aggregation for the 'double' data type. */
	protected static class SingleValueAggregationDouble extends AAggregation<SingleValueFunction> {

		/**
		 * Constructor.
		 *
		 * @param id A String that identifies this operation
		 * @param aggFun The {@link SingleValueFunction} that created this aggregation
		 * @param dataType The data {@link Types type} of the data elements that will be aggregated
		 */
		public SingleValueAggregationDouble(final String id, final SingleValueFunction aggFun, final int dataType) {
			super(id, aggFun, dataType);
		}

		@Override
		public IAggregationBinding bindSource(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingDouble(source, (IWritableArray) destination);
		}

		@Override
		public IAggregationBinding bindAggregates(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingDouble(source, (IWritableArray) destination);
		}

	}

	/** Single value aggregation for the 'float' data type. */
	protected static class SingleValueAggregationFloat extends AAggregation<SingleValueFunction> {

		/**
		 * Constructor.
		 *
		 * @param id A String that identifies this operation
		 * @param aggFun The {@link SingleValueFunction} that created this aggregation
		 * @param dataType The data {@link Types type} of the data elements that will be aggregated
		 */
		public SingleValueAggregationFloat(final String id, final SingleValueFunction aggFun, final int dataType) {
			super(id, aggFun, dataType);
		}

		@Override
		public IAggregationBinding bindSource(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingFloat(source, (IWritableArray) destination);
		}

		@Override
		public IAggregationBinding bindAggregates(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingFloat(source, (IWritableArray) destination);
		}

	}

	/** Single value aggregation for the 'boolean' data type. */
	protected static class SingleValueAggregationBoolean extends AAggregation<SingleValueFunction> {

		/**
		 * Constructor.
		 *
		 * @param id A String that identifies this operation
		 * @param aggFun The {@link SingleValueFunction} that created this aggregation
		 * @param dataType The data {@link Types type} of the data elements that will be aggregated
		 */
		public SingleValueAggregationBoolean(final String id, final SingleValueFunction aggFun, final int dataType) {
			super(id, aggFun, dataType);
		}

		@Override
		public IAggregationBinding bindSource(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingBoolean(source, (IWritableArray) destination);
		}

		@Override
		public IAggregationBinding bindAggregates(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingBoolean(source, (IWritableArray) destination);
		}

	}

	/** Single value aggregation for the 'object' data type. */
	protected static class SingleValueAggregationObject extends AAggregation<SingleValueFunction> {

		/**
		 * Constructor.
		 *
		 * @param id A String that identifies this operation
		 * @param aggFun The {@link SingleValueFunction} that created this aggregation
		 * @param dataType The data {@link Types type} of the data elements that will be aggregated
		 */
		public SingleValueAggregationObject(final String id, final SingleValueFunction aggFun, final int dataType) {
			super(id, aggFun, dataType);
		}

		@Override
		public IAggregationBinding bindSource(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingObject(source, (IWritableArray) destination);
		}

		@Override
		public IAggregationBinding bindAggregates(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingObject(source, (IWritableArray) destination);
		}

	}

	/** Single value aggregation for the 'object' data type. */
	protected static class SingleValueAggregationArrayObject extends AAggregation<SingleValueFunction> {

		/**
		 * Constructor.
		 *
		 * @param id A String that identifies this operation
		 * @param aggFun The {@link SingleValueFunction} that created this aggregation
		 * @param dataType The data {@link Types type} of the data elements that will be aggregated
		 */
		public SingleValueAggregationArrayObject(final String id, final SingleValueFunction aggFun, final int dataType) {
			super(id, aggFun, dataType);
		}

		@Override
		public IAggregationBinding bindSource(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingArrayObject(source, (IWritableArray) destination);
		}

		@Override
		public IAggregationBinding bindAggregates(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingArrayObject(source, (IWritableArray) destination);
		}

	}

	/** Single value aggregation for the nullable 'long' data type. */
	protected static class SingleValueAggregationLongNullable extends AAggregation<SingleValueFunction> {

		/**
		 * Constructor.
		 *
		 * @param id A String that identifies this operation
		 * @param aggFun The {@link SingleValueFunction} that created this aggregation
		 * @param dataType The data {@link Types type} of the data elements that will be aggregated
		 */
		public SingleValueAggregationLongNullable(final String id, final SingleValueFunction aggFun, final int dataType) {
			super(id, aggFun, dataType);
		}

		@Override
		public IAggregationBinding bindSource(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingLongNullable(source, (IWritableArray) destination);
		}

		@Override
		public IAggregationBinding bindAggregates(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingLongNullable(source, (IWritableArray) destination);
		}

	}

	/** Single value aggregation for the Nullable 'int' data type. */
	protected static class SingleValueAggregationIntNullable extends AAggregation<SingleValueFunction> {

		/**
		 * Constructor.
		 *
		 * @param id A String that identifies this operation
		 * @param aggFun The {@link SingleValueFunction} that created this aggregation
		 * @param dataType The data {@link Types type} of the data elements that will be aggregated
		 */
		public SingleValueAggregationIntNullable(final String id, final SingleValueFunction aggFun, final int dataType) {
			super(id, aggFun, dataType);
		}

		@Override
		public IAggregationBinding bindSource(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingIntNullable(source, (IWritableArray) destination);
		}

		@Override
		public IAggregationBinding bindAggregates(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingIntNullable(source, (IWritableArray) destination);
		}

	}

	/** Single value aggregation for the Nullable 'double' data type. */
	protected static class SingleValueAggregationDoubleNullable extends AAggregation<SingleValueFunction> {

		/**
		 * Constructor.
		 *
		 * @param id A String that identifies this operation
		 * @param aggFun The {@link SingleValueFunction} that created this aggregation
		 * @param dataType The data {@link Types type} of the data elements that will be aggregated
		 */
		public SingleValueAggregationDoubleNullable(final String id, final SingleValueFunction aggFun, final int dataType) {
			super(id, aggFun, dataType);
		}

		@Override
		public IAggregationBinding bindSource(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingDoubleNullable(source, (IWritableArray) destination);
		}

		@Override
		public IAggregationBinding bindAggregates(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingDoubleNullable(source, (IWritableArray) destination);
		}

	}

	/** Single value aggregation for the Nullable 'float' data type. */
	protected static class SingleValueAggregationFloatNullable extends AAggregation<SingleValueFunction> {

		/**
		 * Constructor.
		 *
		 * @param id A String that identifies this operation
		 * @param aggFun The {@link SingleValueFunction} that created this aggregation
		 * @param dataType The data {@link Types type} of the data elements that will be aggregated
		 */
		public SingleValueAggregationFloatNullable(final String id, final SingleValueFunction aggFun, final int dataType) {
			super(id, aggFun, dataType);
		}

		@Override
		public IAggregationBinding bindSource(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingFloatNullable(source, (IWritableArray) destination);
		}

		@Override
		public IAggregationBinding bindAggregates(IArrayReader source, IArrayWriter destination) {
			return new SingleValueBindingFloatNullable(source, (IWritableArray) destination);
		}

	}

	// BINDINGS

	/** Abstract single value binding. */
	protected abstract static class ASingleValueBinding extends AAggregationBinding {

		/** Input data. */
		protected final IArrayReader input;

		/** Output aggregates. */
		protected final IWritableArray output;

		/**
		 * Constructor.
		 *
		 * @param input input column
		 * @param output output column
		 */
		protected ASingleValueBinding(IArrayReader input, IWritableArray output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public void disaggregate(int from, int to) {
			throw new UnsupportedOperationException(PLUGIN_KEY + " aggregation function is append-only, it does not support removals and disaggregation.");
		}

	}

	/** Binding for the 'long' data type. */
	public static class SingleValueBindingLong extends ASingleValueBinding {

		/**
		 * Constructor.
		 *
		 * @param input input column
		 * @param output output column
		 */
		protected SingleValueBindingLong(IArrayReader input, IWritableArray output) {
			super(input, output);
		}

		@Override
		public void copy(int from, int to) {
			output.writeLong(to, input.readLong(from));
		}

		@Override
		public void aggregate(int from, int to) {
			long out = output.readLong(to);
			long in = input.readLong(from);
			// Both values are expected to be equal
			if(in != out) {
		//		throw new ActiveViamRuntimeException(String.format(AGGREGATE_MESSAGE, String.valueOf(out), String.valueOf(in)));
			}
		}

	}

	/** Binding for the 'int' data type. */
	public static class SingleValueBindingInteger extends ASingleValueBinding {

		/**
		 * Constructor.
		 *
		 * @param input input column
		 * @param output output column
		 */
		protected SingleValueBindingInteger(IArrayReader input, IWritableArray output) {
			super(input, output);
		}

		@Override
		public void copy(int from, int to) {
			output.writeInt(to, input.readInt(from));
		}

		@Override
		public void aggregate(int from, int to) {
			int out = output.readInt(to);
			int in = input.readInt(from);
			// Both values are expected to be equal
			if(in != out) {
			//	throw new ActiveViamRuntimeException(String.format(AGGREGATE_MESSAGE, String.valueOf(out), String.valueOf(in)));
			}
		}

	}

	/** Binding for the 'double' data type. */
	public static class SingleValueBindingDouble extends ASingleValueBinding {

		/**
		 * Constructor.
		 *
		 * @param input input column
		 * @param output output column
		 */
		protected SingleValueBindingDouble(IArrayReader input, IWritableArray output) {
			super(input, output);
		}

		@Override
		public void copy(int from, int to) {
			output.writeDouble(to, input.readDouble(from));
		}

		@Override
		public void aggregate(int from, int to) {
			double out = output.readDouble(to);
			double in = input.readDouble(from);
			// Both values are expected to be equal
			if(Double.compare(out, in) != 0) {
			//	throw new ActiveViamRuntimeException(String.format(AGGREGATE_MESSAGE, String.valueOf(out), String.valueOf(in)));
			}
		}

	}

	/** Binding for the 'float' data type. */
	public static class SingleValueBindingFloat extends ASingleValueBinding {

		/**
		 * Constructor.
		 *
		 * @param input input column
		 * @param output output column
		 */
		protected SingleValueBindingFloat(IArrayReader input, IWritableArray output) {
			super(input, output);
		}

		@Override
		public void copy(int from, int to) {
			output.writeFloat(to, input.readFloat(from));
		}

		@Override
		public void aggregate(int from, int to) {
			float out = output.readFloat(to);
			float in = input.readFloat(from);
			// Both values are expected to be equal
			if(Float.compare(out, in) != 0) {
			//	throw new ActiveViamRuntimeException(String.format(AGGREGATE_MESSAGE, String.valueOf(out), String.valueOf(in)));
			}
		}

	}

	/** Binding for the 'boolean' data type. */
	public static class SingleValueBindingBoolean extends ASingleValueBinding {

		/**
		 * Constructor.
		 *
		 * @param input input column
		 * @param output output column
		 */
		protected SingleValueBindingBoolean(IArrayReader input, IWritableArray output) {
			super(input, output);
		}

		@Override
		public void copy(int from, int to) {
			output.writeBoolean(to, input.readBoolean(from));
		}

		@Override
		public void aggregate(int from, int to) {
			boolean out = output.readBoolean(to);
			boolean in = input.readBoolean(from);
			// Both values are expected to be equal
			if(out != in) {
			//	throw new ActiveViamRuntimeException(String.format(AGGREGATE_MESSAGE, String.valueOf(out), String.valueOf(in)));
			}
		}

	}

	/** Binding for the 'array of objects' data type. */
	public static class SingleValueBindingArrayObject extends ASingleValueBinding {

		/**
		 * Constructor.
		 *
		 * @param input input column
		 * @param output output column
		 */
		protected SingleValueBindingArrayObject(IArrayReader input, IWritableArray output) {
			super(input, output);
		}

		@Override
		public void copy(int from, int to) {
			output.write(to, input.read(from));
		}

		@Override
		public void aggregate(int from, int to) {
			Object out = output.read(to);
			Object in = input.read(from);
			if (in instanceof IVector) {
				if (!in.equals(out)) {
					throw new ActiveViamRuntimeException(
							String.format(AGGREGATE_MESSAGE, out, in));
				}
			} else {
				Object[] outArray = (Object[]) out;
				Object[] inArray = (Object[]) in;
				if (!Arrays.equals(outArray, inArray)) {
				//	throw new ActiveViamRuntimeException(
				//			String.format(
				//					AGGREGATE_MESSAGE, Arrays.toString(outArray), Arrays.toString(inArray)));
				}
			}
		}

	}

	/** Binding for the 'object' data type. */
	public static class SingleValueBindingObject extends ASingleValueBinding {

		/**
		 * Constructor.
		 *
		 * @param input input column
		 * @param output output column
		 */
		protected SingleValueBindingObject(IArrayReader input, IWritableArray output) {
			super(input, output);
		}

		@Override
		public void copy(int from, int to) {
			output.write(to, input.read(from));
		}

		@Override
		public void aggregate(int from, int to) {
			Object out = output.read(to);
			Object in = input.read(from);
			// Both values are expected to be equal
			if(!Objects.equals(out, in)) {
			//	throw new ActiveViamRuntimeException(String.format(AGGREGATE_MESSAGE, String.valueOf(out), String.valueOf(in)));
			}
		}

	}

	/** Binding for the nullable 'long' data type. */
	public static class SingleValueBindingLongNullable extends ASingleValueBinding {

		/**
		 * Constructor.
		 *
		 * @param input input column
		 * @param output output column
		 */
		protected SingleValueBindingLongNullable(IArrayReader input, IWritableArray output) {
			super(input, output);
		}

		@Override
		public void copy(int from, int to) {
			if (input.isNull(from)) {
				output.write(to, null);
			} else {
				output.writeLong(to, input.readLong(from));
			}
		}

		@Override
		public void aggregate(int from, int to) {
			if (!input.isNull(from)) {
				long in = input.readLong(from);
				if(output.isNull(to)) {
					output.writeLong(to, in);
				} else {
					long out = output.readLong(to);
					if(in != out) {
						throw new ActiveViamRuntimeException(String.format(AGGREGATE_MESSAGE, String.valueOf(out), String.valueOf(in)));
					}
				}
			}
		}

	}

	/** Binding for the nullable 'int' data type. */
	public static class SingleValueBindingIntNullable extends ASingleValueBinding {

		/**
		 * Constructor.
		 *
		 * @param input input column
		 * @param output output column
		 */
		protected SingleValueBindingIntNullable(IArrayReader input, IWritableArray output) {
			super(input, output);
		}

		@Override
		public void copy(int from, int to) {
			if (input.isNull(from)) {
				output.write(to, null);
			} else {
				output.writeInt(to, input.readInt(from));
			}
		}

		@Override
		public void aggregate(int from, int to) {
			if (!input.isNull(from)) {
				int in = input.readInt(from);
				if(output.isNull(to)) {
					output.writeInt(to, in);
				} else {
					int out = output.readInt(to);
					if(in != out) {
					//	throw new ActiveViamRuntimeException(String.format(AGGREGATE_MESSAGE, String.valueOf(out), String.valueOf(in)));
					}
				}
			}
		}

	}

	/** Binding for the nullable 'double' data type. */
	public static class SingleValueBindingDoubleNullable extends ASingleValueBinding {

		/**
		 * Constructor.
		 *
		 * @param input input column
		 * @param output output column
		 */
		protected SingleValueBindingDoubleNullable(IArrayReader input, IWritableArray output) {
			super(input, output);
		}

		@Override
		public void copy(int from, int to) {
			if (input.isNull(from)) {
				output.write(to, null);
			} else {
				output.writeDouble(to, input.readDouble(from));
			}
		}

		@Override
		public void aggregate(int from, int to) {
			if (!input.isNull(from)) {
				double in = input.readDouble(from);
				if(output.isNull(to)) {
					output.writeDouble(to, in);
				} else {
					double out = output.readDouble(to);
					if(Double.compare(in, out) != 0) {
					//	throw new ActiveViamRuntimeException(String.format(AGGREGATE_MESSAGE, String.valueOf(out), String.valueOf(in)));
					}
				}
			}
		}

	}

	/** Binding for the nullable 'float' data type. */
	public static class SingleValueBindingFloatNullable extends ASingleValueBinding {

		/**
		 * Constructor.
		 *
		 * @param input input column
		 * @param output output column
		 */
		protected SingleValueBindingFloatNullable(IArrayReader input, IWritableArray output) {
			super(input, output);
		}

		@Override
		public void copy(int from, int to) {
			if (input.isNull(from)) {
				output.write(to, null);
			} else {
				output.writeFloat(to, input.readFloat(from));
			}
		}

		@Override
		public void aggregate(int from, int to) {
			if (!input.isNull(from)) {
				float in = input.readFloat(from);
				if(output.isNull(to)) {
					output.writeFloat(to, in);
				} else {
					float out = output.readFloat(to);
					if(Float.compare(in, out) != 0) {
					//	throw new ActiveViamRuntimeException(String.format(AGGREGATE_MESSAGE, String.valueOf(out), String.valueOf(in)));
					}
				}
			}
		}

	}

}
