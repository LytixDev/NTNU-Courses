local A=10 B=20 C=30 in
    {System.show C}

    thread
        {System.show A}
        {Delay 100}
        {System.show A * 10}
    end

    thread
        {System.show B}
        {Delay 100}
        {System.show B * 10}
    end

    {System.show C * 100}
end