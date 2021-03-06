// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: request.proto

package wg_test.chat.proto;

public final class Request {
  private Request() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface ClientMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:wg_test.ClientMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>.wg_test.ClientMessage.MessageType type = 1;</code>
     */
    int getTypeValue();
    /**
     * <code>.wg_test.ClientMessage.MessageType type = 1;</code>
     */
    wg_test.chat.proto.Request.ClientMessage.MessageType getType();

    /**
     * <code>string token = 2;</code>
     */
    java.lang.String getToken();
    /**
     * <code>string token = 2;</code>
     */
    com.google.protobuf.ByteString
        getTokenBytes();

    /**
     * <code>bytes data = 3;</code>
     */
    com.google.protobuf.ByteString getData();
  }
  /**
   * Protobuf type {@code wg_test.ClientMessage}
   */
  public  static final class ClientMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:wg_test.ClientMessage)
      ClientMessageOrBuilder {
    // Use ClientMessage.newBuilder() to construct.
    private ClientMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private ClientMessage() {
      type_ = 0;
      token_ = "";
      data_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }
    private ClientMessage(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!input.skipField(tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              int rawValue = input.readEnum();

              type_ = rawValue;
              break;
            }
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();

              token_ = s;
              break;
            }
            case 26: {

              data_ = input.readBytes();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return wg_test.chat.proto.Request.internal_static_wg_test_ClientMessage_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return wg_test.chat.proto.Request.internal_static_wg_test_ClientMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              wg_test.chat.proto.Request.ClientMessage.class, wg_test.chat.proto.Request.ClientMessage.Builder.class);
    }

    /**
     * Protobuf enum {@code wg_test.ClientMessage.MessageType}
     */
    public enum MessageType
        implements com.google.protobuf.ProtocolMessageEnum {
      /**
       * <code>AUTH = 0;</code>
       */
      AUTH(0),
      /**
       * <code>REGISTER = 1;</code>
       */
      REGISTER(1),
      /**
       * <code>CHECK_USER_NAME = 2;</code>
       */
      CHECK_USER_NAME(2),
      /**
       * <code>RECONNECT = 3;</code>
       */
      RECONNECT(3),
      /**
       * <code>REGENERATE_TOKEN = 4;</code>
       */
      REGENERATE_TOKEN(4),
      /**
       * <code>USER_LIST = 10;</code>
       */
      USER_LIST(10),
      /**
       * <code>SEND_MESSAGE = 20;</code>
       */
      SEND_MESSAGE(20),
      /**
       * <code>UPDATE_MESSAGE = 21;</code>
       */
      UPDATE_MESSAGE(21),
      /**
       * <code>DELETE_MESSAGE = 22;</code>
       */
      DELETE_MESSAGE(22),
      /**
       * <code>LIST_MESSAGES = 23;</code>
       */
      LIST_MESSAGES(23),
      UNRECOGNIZED(-1),
      ;

      /**
       * <code>AUTH = 0;</code>
       */
      public static final int AUTH_VALUE = 0;
      /**
       * <code>REGISTER = 1;</code>
       */
      public static final int REGISTER_VALUE = 1;
      /**
       * <code>CHECK_USER_NAME = 2;</code>
       */
      public static final int CHECK_USER_NAME_VALUE = 2;
      /**
       * <code>RECONNECT = 3;</code>
       */
      public static final int RECONNECT_VALUE = 3;
      /**
       * <code>REGENERATE_TOKEN = 4;</code>
       */
      public static final int REGENERATE_TOKEN_VALUE = 4;
      /**
       * <code>USER_LIST = 10;</code>
       */
      public static final int USER_LIST_VALUE = 10;
      /**
       * <code>SEND_MESSAGE = 20;</code>
       */
      public static final int SEND_MESSAGE_VALUE = 20;
      /**
       * <code>UPDATE_MESSAGE = 21;</code>
       */
      public static final int UPDATE_MESSAGE_VALUE = 21;
      /**
       * <code>DELETE_MESSAGE = 22;</code>
       */
      public static final int DELETE_MESSAGE_VALUE = 22;
      /**
       * <code>LIST_MESSAGES = 23;</code>
       */
      public static final int LIST_MESSAGES_VALUE = 23;


      public final int getNumber() {
        if (this == UNRECOGNIZED) {
          throw new java.lang.IllegalArgumentException(
              "Can't get the number of an unknown enum value.");
        }
        return value;
      }

      /**
       * @deprecated Use {@link #forNumber(int)} instead.
       */
      @java.lang.Deprecated
      public static MessageType valueOf(int value) {
        return forNumber(value);
      }

      public static MessageType forNumber(int value) {
        switch (value) {
          case 0: return AUTH;
          case 1: return REGISTER;
          case 2: return CHECK_USER_NAME;
          case 3: return RECONNECT;
          case 4: return REGENERATE_TOKEN;
          case 10: return USER_LIST;
          case 20: return SEND_MESSAGE;
          case 21: return UPDATE_MESSAGE;
          case 22: return DELETE_MESSAGE;
          case 23: return LIST_MESSAGES;
          default: return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<MessageType>
          internalGetValueMap() {
        return internalValueMap;
      }
      private static final com.google.protobuf.Internal.EnumLiteMap<
          MessageType> internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<MessageType>() {
              public MessageType findValueByNumber(int number) {
                return MessageType.forNumber(number);
              }
            };

      public final com.google.protobuf.Descriptors.EnumValueDescriptor
          getValueDescriptor() {
        return getDescriptor().getValues().get(ordinal());
      }
      public final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptorForType() {
        return getDescriptor();
      }
      public static final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptor() {
        return wg_test.chat.proto.Request.ClientMessage.getDescriptor().getEnumTypes().get(0);
      }

      private static final MessageType[] VALUES = values();

      public static MessageType valueOf(
          com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != getDescriptor()) {
          throw new java.lang.IllegalArgumentException(
            "EnumValueDescriptor is not for this type.");
        }
        if (desc.getIndex() == -1) {
          return UNRECOGNIZED;
        }
        return VALUES[desc.getIndex()];
      }

      private final int value;

      private MessageType(int value) {
        this.value = value;
      }

      // @@protoc_insertion_point(enum_scope:wg_test.ClientMessage.MessageType)
    }

    public static final int TYPE_FIELD_NUMBER = 1;
    private int type_;
    /**
     * <code>.wg_test.ClientMessage.MessageType type = 1;</code>
     */
    public int getTypeValue() {
      return type_;
    }
    /**
     * <code>.wg_test.ClientMessage.MessageType type = 1;</code>
     */
    public wg_test.chat.proto.Request.ClientMessage.MessageType getType() {
      wg_test.chat.proto.Request.ClientMessage.MessageType result = wg_test.chat.proto.Request.ClientMessage.MessageType.valueOf(type_);
      return result == null ? wg_test.chat.proto.Request.ClientMessage.MessageType.UNRECOGNIZED : result;
    }

    public static final int TOKEN_FIELD_NUMBER = 2;
    private volatile java.lang.Object token_;
    /**
     * <code>string token = 2;</code>
     */
    public java.lang.String getToken() {
      java.lang.Object ref = token_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        token_ = s;
        return s;
      }
    }
    /**
     * <code>string token = 2;</code>
     */
    public com.google.protobuf.ByteString
        getTokenBytes() {
      java.lang.Object ref = token_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        token_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int DATA_FIELD_NUMBER = 3;
    private com.google.protobuf.ByteString data_;
    /**
     * <code>bytes data = 3;</code>
     */
    public com.google.protobuf.ByteString getData() {
      return data_;
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (type_ != wg_test.chat.proto.Request.ClientMessage.MessageType.AUTH.getNumber()) {
        output.writeEnum(1, type_);
      }
      if (!getTokenBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, token_);
      }
      if (!data_.isEmpty()) {
        output.writeBytes(3, data_);
      }
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (type_ != wg_test.chat.proto.Request.ClientMessage.MessageType.AUTH.getNumber()) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(1, type_);
      }
      if (!getTokenBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, token_);
      }
      if (!data_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, data_);
      }
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof wg_test.chat.proto.Request.ClientMessage)) {
        return super.equals(obj);
      }
      wg_test.chat.proto.Request.ClientMessage other = (wg_test.chat.proto.Request.ClientMessage) obj;

      boolean result = true;
      result = result && type_ == other.type_;
      result = result && getToken()
          .equals(other.getToken());
      result = result && getData()
          .equals(other.getData());
      return result;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + TYPE_FIELD_NUMBER;
      hash = (53 * hash) + type_;
      hash = (37 * hash) + TOKEN_FIELD_NUMBER;
      hash = (53 * hash) + getToken().hashCode();
      hash = (37 * hash) + DATA_FIELD_NUMBER;
      hash = (53 * hash) + getData().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static wg_test.chat.proto.Request.ClientMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static wg_test.chat.proto.Request.ClientMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static wg_test.chat.proto.Request.ClientMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static wg_test.chat.proto.Request.ClientMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static wg_test.chat.proto.Request.ClientMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static wg_test.chat.proto.Request.ClientMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static wg_test.chat.proto.Request.ClientMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static wg_test.chat.proto.Request.ClientMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static wg_test.chat.proto.Request.ClientMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static wg_test.chat.proto.Request.ClientMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static wg_test.chat.proto.Request.ClientMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static wg_test.chat.proto.Request.ClientMessage parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(wg_test.chat.proto.Request.ClientMessage prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code wg_test.ClientMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:wg_test.ClientMessage)
        wg_test.chat.proto.Request.ClientMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return wg_test.chat.proto.Request.internal_static_wg_test_ClientMessage_descriptor;
      }

      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return wg_test.chat.proto.Request.internal_static_wg_test_ClientMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                wg_test.chat.proto.Request.ClientMessage.class, wg_test.chat.proto.Request.ClientMessage.Builder.class);
      }

      // Construct using wg_test.chat.proto.Request.ClientMessage.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        type_ = 0;

        token_ = "";

        data_ = com.google.protobuf.ByteString.EMPTY;

        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return wg_test.chat.proto.Request.internal_static_wg_test_ClientMessage_descriptor;
      }

      public wg_test.chat.proto.Request.ClientMessage getDefaultInstanceForType() {
        return wg_test.chat.proto.Request.ClientMessage.getDefaultInstance();
      }

      public wg_test.chat.proto.Request.ClientMessage build() {
        wg_test.chat.proto.Request.ClientMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public wg_test.chat.proto.Request.ClientMessage buildPartial() {
        wg_test.chat.proto.Request.ClientMessage result = new wg_test.chat.proto.Request.ClientMessage(this);
        result.type_ = type_;
        result.token_ = token_;
        result.data_ = data_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof wg_test.chat.proto.Request.ClientMessage) {
          return mergeFrom((wg_test.chat.proto.Request.ClientMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(wg_test.chat.proto.Request.ClientMessage other) {
        if (other == wg_test.chat.proto.Request.ClientMessage.getDefaultInstance()) return this;
        if (other.type_ != 0) {
          setTypeValue(other.getTypeValue());
        }
        if (!other.getToken().isEmpty()) {
          token_ = other.token_;
          onChanged();
        }
        if (other.getData() != com.google.protobuf.ByteString.EMPTY) {
          setData(other.getData());
        }
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        wg_test.chat.proto.Request.ClientMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (wg_test.chat.proto.Request.ClientMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int type_ = 0;
      /**
       * <code>.wg_test.ClientMessage.MessageType type = 1;</code>
       */
      public int getTypeValue() {
        return type_;
      }
      /**
       * <code>.wg_test.ClientMessage.MessageType type = 1;</code>
       */
      public Builder setTypeValue(int value) {
        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>.wg_test.ClientMessage.MessageType type = 1;</code>
       */
      public wg_test.chat.proto.Request.ClientMessage.MessageType getType() {
        wg_test.chat.proto.Request.ClientMessage.MessageType result = wg_test.chat.proto.Request.ClientMessage.MessageType.valueOf(type_);
        return result == null ? wg_test.chat.proto.Request.ClientMessage.MessageType.UNRECOGNIZED : result;
      }
      /**
       * <code>.wg_test.ClientMessage.MessageType type = 1;</code>
       */
      public Builder setType(wg_test.chat.proto.Request.ClientMessage.MessageType value) {
        if (value == null) {
          throw new NullPointerException();
        }
        
        type_ = value.getNumber();
        onChanged();
        return this;
      }
      /**
       * <code>.wg_test.ClientMessage.MessageType type = 1;</code>
       */
      public Builder clearType() {
        
        type_ = 0;
        onChanged();
        return this;
      }

      private java.lang.Object token_ = "";
      /**
       * <code>string token = 2;</code>
       */
      public java.lang.String getToken() {
        java.lang.Object ref = token_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          token_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string token = 2;</code>
       */
      public com.google.protobuf.ByteString
          getTokenBytes() {
        java.lang.Object ref = token_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          token_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string token = 2;</code>
       */
      public Builder setToken(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        token_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string token = 2;</code>
       */
      public Builder clearToken() {
        
        token_ = getDefaultInstance().getToken();
        onChanged();
        return this;
      }
      /**
       * <code>string token = 2;</code>
       */
      public Builder setTokenBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        token_ = value;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString data_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes data = 3;</code>
       */
      public com.google.protobuf.ByteString getData() {
        return data_;
      }
      /**
       * <code>bytes data = 3;</code>
       */
      public Builder setData(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        data_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes data = 3;</code>
       */
      public Builder clearData() {
        
        data_ = getDefaultInstance().getData();
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }


      // @@protoc_insertion_point(builder_scope:wg_test.ClientMessage)
    }

    // @@protoc_insertion_point(class_scope:wg_test.ClientMessage)
    private static final wg_test.chat.proto.Request.ClientMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new wg_test.chat.proto.Request.ClientMessage();
    }

    public static wg_test.chat.proto.Request.ClientMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<ClientMessage>
        PARSER = new com.google.protobuf.AbstractParser<ClientMessage>() {
      public ClientMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new ClientMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<ClientMessage> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<ClientMessage> getParserForType() {
      return PARSER;
    }

    public wg_test.chat.proto.Request.ClientMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_wg_test_ClientMessage_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_wg_test_ClientMessage_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\rrequest.proto\022\007wg_test\"\234\002\n\rClientMessa" +
      "ge\0220\n\004type\030\001 \001(\0162\".wg_test.ClientMessage" +
      ".MessageType\022\r\n\005token\030\002 \001(\t\022\014\n\004data\030\003 \001(" +
      "\014\"\273\001\n\013MessageType\022\010\n\004AUTH\020\000\022\014\n\010REGISTER\020" +
      "\001\022\023\n\017CHECK_USER_NAME\020\002\022\r\n\tRECONNECT\020\003\022\024\n" +
      "\020REGENERATE_TOKEN\020\004\022\r\n\tUSER_LIST\020\n\022\020\n\014SE" +
      "ND_MESSAGE\020\024\022\022\n\016UPDATE_MESSAGE\020\025\022\022\n\016DELE" +
      "TE_MESSAGE\020\026\022\021\n\rLIST_MESSAGES\020\027B\024\n\022wg_te" +
      "st.chat.protob\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_wg_test_ClientMessage_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_wg_test_ClientMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_wg_test_ClientMessage_descriptor,
        new java.lang.String[] { "Type", "Token", "Data", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
