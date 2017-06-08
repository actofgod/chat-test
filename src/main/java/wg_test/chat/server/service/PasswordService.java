package wg_test.chat.server.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Сервис для работы с паролями пользователей
 */
public class PasswordService
{
    /**
     * Инстанс для генерации хэшей паролей пользователей
     */
    private MessageDigest md;

    /**
     * Генератор случайных последовательностей для генерации соли паролей
     */
    private SecureRandom randomGenerator;

    /**
     * Длина соли паролей пользователей
     */
    private byte saltLength;

    /**
     * @param algorithm Используемый алгоритм хэширования паролей
     * @param saltLength Длина соли паролей пользователей
     */
    public PasswordService(String algorithm, byte saltLength)
    {
        try {
            this.md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {}
        this.saltLength = saltLength;
        this.randomGenerator = new SecureRandom();
    }

    /**
     * Сравнивает переданный пароль с хэшированным паролем пользователя
     * @param plainPassword Пароль в открытом виде
     * @param passwordHash Хэш пароля пользователя
     * @param passwordSalt Соль пароля пользователя
     * @return True если пароль верный, false если нет
     */
    public boolean comparePassword(String plainPassword, byte[] passwordHash, byte[] passwordSalt)
    {
        byte[] hashed = hashPassword(plainPassword, passwordSalt);
        return Arrays.equals(passwordHash, hashed);
    }

    /**
     * Хэширует пароль пользователя
     * @param plainPassword Пароль пользователя в открытом виде
     * @param salt Соль пароля пользователя
     * @return Хэш пароля пользователя в виде массива байтов
     */
    public synchronized byte[] hashPassword(String plainPassword, byte[] salt)
    {
        MessageDigest md = getMessageDigest();
        md.update(plainPassword.getBytes());
        return md.digest(salt);
    }

    /**
     * Генерирует соль пароля нового пользователя
     * @return Соль пароля
     */
    public synchronized byte[] generateSalt()
    {
        byte[] result = new byte[this.saltLength];
        this.randomGenerator.nextBytes(result);
        return result;
    }

    /**
     * Возвращает инстанс для хэширования паролей
     * @return Инстанс для хэширования паролей
     */
    private MessageDigest getMessageDigest()
    {
        md.reset();
        return md;
    }
}
