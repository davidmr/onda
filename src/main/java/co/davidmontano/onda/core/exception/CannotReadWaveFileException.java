package co.davidmontano.onda.core.exception;

/**
 * Copyright 2015 David Montaño
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, softwar
 *  distributed under the License is distributed on an "AS IS" BASIS
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 *  See the License for the specific language governing permissions an
 *  limitations under the License.
 */
public class CannotReadWaveFileException extends RuntimeException {

    public CannotReadWaveFileException(Exception e) {
        super(e);
    }
}
