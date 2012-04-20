# -*- encoding : utf-8 -*-

# Moduł zawierający wyjątki zgłaszane w aplikacji
module Exceptions

  # Brak jednostki (dodawanie nowego zgłoszenia)
  class NoUnitForPoint < StandardError
    # Tworzy nowy wyjątek i ustawia odpowiedni komunikat
    def initialize
      super("Nie ma jednostki dla takiego punktu")
    end
  end

  # Nie podano wymaganego argumentu(ów) (dodawanie nowego zgłoszenia)
  class NilArguments < StandardError
    # Tworzy nowy wyjątek i ustawia odpowiedni komunikat
    def initialize
      super("Nie podano wymaganego argumentu/argumentów")
    end
  end

  # Nieznana kategoria (dodawanie nowego zgłoszenia)
  class UnknownCategory < StandardError
    # Tworzy nowy wyjątek i ustawia odpowiedni komunikat
    def initialize
      super("Nie ma takiej kategorii")
    end
  end

  # Nieprawidłowy adres e-mail (dodawanie nowego zgłoszenia)
  class IncorrectNotificarEmail < StandardError
    # Tworzy nowy wyjątek i ustawia odpowiedni komunikat
    def initialize
      super("Niepoprawny adres e-mail")
    end
  end

  # Bład geokodowania adresu (dodawanie nowego zgłoszenia)
  class GeocodingException < StandardError
    # Tworzy nowy wyjątek i ustawia odpowiedni komunikat
    def initialize
      super("Błąd przy ustalaniu adresu na podstawie współrzędnych")
    end
  end

  # Nieznane zgłoszenie (złączanie zgłoszeń)
  class UnknownIssue < StandardError
    # Tworzy nowy wyjątek i ustawia odpowiedni komunikat
    def initialize
      super("Nieznane zgłoszenie")
    end
  end

end
