local A B C in
    thread
        A = 2
        {System.show A}
    end
    thread
        B = A * 10
        {System.show B}
    end
    C = A + B
    {System.show C}
end